package com.verygoodsecurity.vgsshow.core.network.client

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.with
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
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
            .header(AGENT, TEMPORARY_STR_AGENT).also {
                addHeaders(it, request.headers)
                setMethod(it, request.method, request.data)
            }
            .build()
    }

    private fun addHeaders(request: Request.Builder, headers: Map<String, String>?) {
        headers?.forEach {
            request.addHeader(it.key, it.value)
        }
    }

    private fun setMethod(request: Request.Builder, method: Method, data: String?) {
        when (method) {
            Method.GET -> request.get()
            Method.POST -> request.post(data?.toRequestBody(MEDIA_TYPE) ?: EMPTY_REQUEST)
        }
    }

    companion object {

        // TODO: Refactor, send media type as parameter to make this class reusable
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