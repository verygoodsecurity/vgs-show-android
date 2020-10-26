package com.verygoodsecurity.vgsshow.core.network.client

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.exception.UrlNotValidException
import com.verygoodsecurity.vgsshow.util.extension.isValidUrl
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.with
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
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
        val url = baseUrl with request.path
        if (!url.isValidUrl()) {
            throw UrlNotValidException()
        }
        return Request.Builder()
            .url(url)
            .header(AGENT, TEMPORARY_STR_AGENT)
            .headers(request.headers)
            .method(request.method, request.data)
            .build()
    }

    private fun Request.Builder.headers(headers: Map<String, String>?): Request.Builder {
        headers?.forEach {
            this.addHeader(it.key, it.value)
        }
        return this
    }

    private fun Request.Builder.method(method: Method, data: String?): Request.Builder {
        when (method) {
            Method.GET -> this.method(method.value, null)
            Method.POST -> this.method(
                method.value,
                data?.toRequestBody(MEDIA_TYPE)
            )
        }
        return this
    }

    companion object {

        private val MEDIA_TYPE = APPLICATION_JSON.toMediaTypeOrNull()
    }

    internal class LoggingInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            with(VGSShow::class.java.simpleName) {
                return chain.proceed(chain.request().also {
                    logDebug(this, "Request{method=${it.method}}")
                }).also {
                    logDebug(this, "Response{code=${it.code}, message=${it.message}}")
                }
            }
        }
    }
}