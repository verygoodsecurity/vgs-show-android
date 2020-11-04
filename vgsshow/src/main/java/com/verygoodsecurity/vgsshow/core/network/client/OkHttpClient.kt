package com.verygoodsecurity.vgsshow.core.network.client

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.addHeaders
import com.verygoodsecurity.vgsshow.core.network.client.extension.setMethod
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.concatWithSlash
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient as OkHttp3Client

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
internal class OkHttpClient constructor(private val baseUrl: String) : IHttpClient {

    private val client: OkHttp3Client by lazy {
        OkHttp3Client().newBuilder()
            .addInterceptor(LoggingInterceptor())
            .callTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }

    override fun execute(request: HttpRequest): HttpResponse {
        return client.newCall(buildOkHttpRequest(request)).execute().toHttpResponse()
    }

    override fun enqueue(request: HttpRequest, callback: HttpRequestCallback) {
        client.newCall(buildOkHttpRequest(request)).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onResponse(response.toHttpResponse())
            }
        })
    }

    override fun cancelAll() {
        client.dispatcher.cancelAll()
    }

    @Throws(Exception::class)
    private fun buildOkHttpRequest(request: HttpRequest): Request {
        return Request.Builder()
            .url(URL(baseUrl concatWithSlash request.path))
            .addHeaders(request.headers)
            .setMethod(
                request.method,
                request.data,
                request.format.toContentType().toMediaTypeOrNull()
            )
            .build()
    }

    internal class LoggingInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            with(VGSShow::class.java.simpleName) {
                return chain.proceed(chain.request().also {
                    logDebug("Request{method=${it.method}}", VGSShow::class.simpleName)
                }).also {
                    logDebug(
                        "Response{code=${it.code}, message=${it.message}}",
                        VGSShow::class.simpleName
                    )
                }
            }
        }
    }
}