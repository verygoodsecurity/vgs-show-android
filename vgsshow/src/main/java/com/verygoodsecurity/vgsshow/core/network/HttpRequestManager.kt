package com.verygoodsecurity.vgsshow.core.network

import android.os.NetworkOnMainThreadException
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.client.okhttp.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.headers.StaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.response.EmptyResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.response.JsonResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import org.json.JSONException
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

/**
 * An implementation of [IHttpRequestManager] that uses [OkHttpClient] to make requests.
 * @suppress Not for public use.
 *
 * @param baseUrl The base URL for all requests.
 * @param headersStore The store for static headers.
 * @param isLogsEnabled Whether or not to enable logging.
 */
internal class HttpRequestManager(
    private val baseUrl: String,
    private val headersStore: StaticHeadersStore,
    private val isLogsEnabled: Boolean = true
) : IHttpRequestManager {

    private val client: IHttpClient by lazy {
        OkHttpClient(isLogsEnabled)
    }

    override fun execute(request: VGSRequest): VGSResponse {
        return try {
            if (request.isInvalidPayload()) {
                return VGSException.RequestPayload(request.requestFormat).toVGSResponse()
            }
            val response = client.execute(request.toHttpRequest(baseUrl, headersStore.getAll()))
            parseResponse(response, request.responseFormat)
        } catch (e: NetworkOnMainThreadException) {
            throw e
        } catch (e: Exception) {
            parseException(e)
        }
    }

    override fun enqueue(request: VGSRequest, callback: ((VGSResponse) -> Unit)?) {
        if (request.isInvalidPayload()) {
            callback?.invoke(
                VGSException.RequestPayload(request.requestFormat).toVGSResponse()
            )
            return
        }
        with(request.toHttpRequest(baseUrl, headersStore.getAll())) {
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

    override fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit) {
        this.client.setCname(vaultId, cname, cnameResult)
    }

    override fun cancelAll() {
        this.client.cancelAll()
    }

    private fun VGSRequest.isInvalidPayload() = payload != null && !payload.isValid()

    @VisibleForTesting
    @Throws(Exception::class)
    internal fun parseResponse(response: HttpResponse, format: VGSHttpBodyFormat): VGSResponse {
        return with(response) {
            if (!isSuccessful) {
                return@with VGSResponse.Error.create(VGSException.Custom(code, message))
            }
            VGSResponse.Success.create(code, parseResponseData(responseBody ?: "", format))
        }
    }

    @VisibleForTesting
    @Throws(Exception::class)
    internal fun parseResponseData(data: String, format: VGSHttpBodyFormat): ResponseData {
        return when (format) {
            VGSHttpBodyFormat.JSON -> JsonResponseData(data)
            VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> EmptyResponseData()
        }
    }

    @VisibleForTesting
    internal fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        is JSONException -> VGSException.ResponsePayload()
        else -> VGSException.Custom(errorMessage = e.message)
    }).toVGSResponse()
}