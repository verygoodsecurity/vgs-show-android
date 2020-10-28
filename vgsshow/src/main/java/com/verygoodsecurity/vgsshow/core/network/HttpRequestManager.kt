package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.util.extension.toMap
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

internal class HttpRequestManager(
    baseUrl: String,
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
            val response = client.call(request.toHttpRequest())
            // TODO: Create extension in Mapper.kt to map HttpResponse to VGSResponse
            if (response.isSuccessful) {
                VGSResponse.Success(
                    response.code,
                    response.responseBody?.toMap(),
                    response.responseBody
                )
            } else {
                VGSResponse.Error(VGSException.Exception(response.code, response.message))
            }
        } catch (e: Exception) {
            parseException(e)
        }
    }

    private fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        else -> VGSException.Exception(errorMessage = e.message)
    }).toVGSResponse()
}