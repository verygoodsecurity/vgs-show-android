package com.verygoodsecurity.vgsshow.core.network.client

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.addHeaders
import com.verygoodsecurity.vgsshow.core.network.client.extension.setMethod
import com.verygoodsecurity.vgsshow.core.network.client.extension.with
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.*
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.Response
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

    override fun call(request: HttpRequest): HttpResponse =
        client.newCall(buildOkHttpRequest(request)).execute().toHttpResponse()

    @Throws(Exception::class)
    private fun buildOkHttpRequest(request: HttpRequest): Request {
        return Request.Builder()
            .url(URL(baseUrl with request.path))
            .addHeaders(request.headers)
            .setMethod(request.method, request.data, MEDIA_TYPE)
            .build()
    }

    companion object {

        // TODO: Refactor, send media type as parameter to make this class reusable
        private val MEDIA_TYPE = APPLICATION_JSON.toMediaTypeOrNull()
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