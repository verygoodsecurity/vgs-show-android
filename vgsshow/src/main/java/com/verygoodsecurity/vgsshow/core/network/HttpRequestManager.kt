package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.cache.IHttpRequestCacheHelper
import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpRequest
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

internal class HttpRequestManager(
    baseUrl: String,
    private val cacheHelper: IHttpRequestCacheHelper,
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
            client.execute(
                request.toHttpRequest(
                    cacheHelper.getHeaders(),
                    cacheHelper.getData()
                )
            ).toVGSResponse()
        } catch (e: Exception) {
            parseException(e)
        }
    }

    override fun enqueue(request: VGSRequest, callback: (VGSResponse) -> Unit) {
        if (!connectionHelper.isConnectionAvailable()) {
            callback.invoke(VGSException.NoInternetConnection().toVGSResponse())
            return
        }
        with(request.toHttpRequest(cacheHelper.getHeaders(), cacheHelper.getData())) {
            logDebug(this.toString(), VGSShow::class.simpleName)
            client.enqueue(this, object : HttpRequestCallback {

                override fun onResponse(response: HttpResponse) {
                    callback.invoke(response.toVGSResponse())
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

    private fun parseException(e: Exception): VGSResponse = (when (e) {
        is MalformedURLException -> VGSException.UrlNotValid()
        is InterruptedIOException, is TimeoutException -> VGSException.RequestTimeout()
        else -> VGSException.Exception(errorMessage = e.message)
    }).toVGSResponse()
}