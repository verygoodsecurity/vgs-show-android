package com.verygoodsecurity.vgsshow.core.network

import android.os.NetworkOnMainThreadException
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.headers.StaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.response.JsonResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import org.json.JSONObject
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

internal class HttpRequestManager(
    baseUrl: String,
    private val headersStore: StaticHeadersStore
) : IHttpRequestManager {

    private val client: IHttpClient by lazy {
        if (isLollipopOrGreater) OkHttpClient(baseUrl) else HttpUrlClient(baseUrl)
    }

    override fun execute(request: VGSRequest): VGSResponse {
        return try {
            if (request.isInvalidPayload()) {
                return VGSException.PayloadException(request.requestFormat).toVGSResponse()
            }
            val response = client.execute(request.toHttpRequest(headersStore.getAll()))
            parseResponse(response, request.responseFormat)
        } catch (e: NetworkOnMainThreadException) {
            throw e
        } catch (e: Exception) {
            parseException(e)
        }
    }

    override fun enqueue(request: VGSRequest, callback: ((VGSResponse) -> Unit)?) {
        if (request.isInvalidPayload()) {
            callback?.invoke(VGSException.PayloadException(request.requestFormat).toVGSResponse())
            return
        }
        with(request.toHttpRequest(headersStore.getAll())) {
            client.enqueue(this, object : HttpRequestCallback {

                override fun onResponse(response: HttpResponse) {
                    try {
                        callback?.invoke(parseResponse(response, request.responseFormat))
                    } catch (e: Exception) {
                        callback?.invoke(parseException(e))
                    }
                }

                override fun onFailure(e: Exception) {
                    callback?.invoke(parseException(e))
                }
            })
        }
    }

    override fun cancelAll() {
        client.cancelAll()
    }

    private fun VGSRequest.isInvalidPayload() = payload != null && !payload.isValid()

    @VisibleForTesting
    @Throws(Exception::class)
    internal fun parseResponse(response: HttpResponse, format: VGSHttpBodyFormat): VGSResponse {
        return with(response) {
            if (!isSuccessful) {
                return@with VGSResponse.Error.create(VGSException.Exception(code, message))
            }
            VGSResponse.Success.create(
                code,
                parseResponseData(responseBody ?: "", format),
                responseBody
            )
        }
    }

    @VisibleForTesting
    @Throws(Exception::class)
    internal fun parseResponseData(data: String, format: VGSHttpBodyFormat): ResponseData {
        return when (format) {
            VGSHttpBodyFormat.JSON -> JsonResponseData(JSONObject(data))
        }
    }

    @VisibleForTesting
    internal fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        else -> VGSException.Exception(errorMessage = e.message)
    }).toVGSResponse()
}