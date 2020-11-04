package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.cache.IVGSCustomHeaderStore
import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData
import com.verygoodsecurity.vgsshow.core.network.model.data.JsonResponseData
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import org.json.JSONException
import org.json.JSONObject
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

internal class HttpRequestManager(
    baseUrl: String,
    private val headersStore: IVGSCustomHeaderStore,
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
                client.execute(request.toHttpRequest(headersStore.getHeaders())),
                request.responseFormat
            )
        } catch (e: Exception) {
            parseException(e)
        }
    }

    override fun enqueue(request: VGSRequest, callback: (VGSResponse) -> Unit) {
        if (!connectionHelper.isConnectionAvailable()) {
            callback.invoke(VGSException.NoInternetConnection().toVGSResponse())
            return
        }
        with(request.toHttpRequest(headersStore.getHeaders())) {
            client.enqueue(this, object : HttpRequestCallback {

                override fun onResponse(response: HttpResponse) {
                    try {
                        callback.invoke(parseResponse(response, request.responseFormat))
                    } catch (e: Exception) {
                        callback.invoke(parseException(e))
                    }
                }

                override fun onFailure(e: Exception) {
                    callback.invoke(parseException(e))
                }
            })
        }
    }

    override fun cancelAll() {
        client.cancelAll()
    }

    @Throws(Exception::class)
    private fun parseResponse(response: HttpResponse, format: VGSHttpFormat): VGSResponse {
        return with(response) {
            if (!isSuccessful) {
                VGSResponse.Error(VGSException.Exception(code, message))
            }
            VGSResponse.Success(code, parseResponseData(responseBody ?: "", format), responseBody)
        }
    }

    // TODO: Add new types of format and handle it here
    @Throws(Exception::class)
    private fun parseResponseData(data: String, format: VGSHttpFormat): IResponseData {
        return when (format) {
            VGSHttpFormat.JSON -> JsonResponseData(JSONObject(data))
        }
    }

    private fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        is JSONException -> VGSException.JSONException()
        else -> VGSException.Exception(errorMessage = e.message)
    }).toVGSResponse()
}