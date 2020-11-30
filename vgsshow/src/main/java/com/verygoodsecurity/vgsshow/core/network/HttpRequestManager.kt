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
import com.verygoodsecurity.vgsshow.core.network.extension.toDocument
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.headers.IVGSStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.JsonResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.XmlResponseData
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.DOMException
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

internal class HttpRequestManager(
    baseUrl: String,
    private val headersStore: IVGSStaticHeadersStore,
    private val connectionHelper: IConnectionHelper
) : IHttpRequestManager {

    private val client: IHttpClient by lazy {
        if (isLollipopOrGreater) OkHttpClient(baseUrl) else HttpUrlClient(baseUrl)
    }

    override fun execute(request: VGSRequest): VGSResponse {
        if (!connectionHelper.isConnectionAvailable()) {
            return VGSException.NoInternetConnection().toVGSResponse()
        }
        return try {
            parseResponse(
                client.execute(request.toHttpRequest(headersStore.getAll())),
                request.responseFormat
            )
        } catch (e: NetworkOnMainThreadException) {
            throw e
        } catch (e: Exception) {
            parseException(e)
        }
    }

    override fun enqueue(request: VGSRequest, callback: ((VGSResponse) -> Unit)?) {
        if (!connectionHelper.isConnectionAvailable()) {
            callback?.invoke(VGSException.NoInternetConnection().toVGSResponse())
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

    // TODO: Add new types of format and handle it here
    @VisibleForTesting
    @Throws(Exception::class)
    internal fun parseResponseData(data: String, format: VGSHttpBodyFormat): IResponseData {
        return when (format) {
            VGSHttpBodyFormat.JSON -> JsonResponseData(JSONObject(data))
            VGSHttpBodyFormat.XML -> XmlResponseData(data.toDocument())
        }
    }

    @VisibleForTesting
    internal fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        is JSONException -> VGSException.ResponseFormatException()
        is DOMException -> VGSException.ResponseFormatException()
        else -> VGSException.Exception(errorMessage = e.message)
    }).toVGSResponse()

    companion object {

        val NETWORK_RESPONSE_CODES = 200..999
    }
}