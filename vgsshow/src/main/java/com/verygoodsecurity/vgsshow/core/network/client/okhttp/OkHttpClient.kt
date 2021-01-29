package com.verygoodsecurity.vgsshow.core.network.client.okhttp

import android.os.Build
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgsshow.core.network.client.CONNECTION_TIME_OUT
import com.verygoodsecurity.vgsshow.core.network.client.CONTENT_TYPE
import com.verygoodsecurity.vgsshow.core.network.client.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.extension.addHeaders
import com.verygoodsecurity.vgsshow.core.network.client.extension.setMethod
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor.CnameInterceptor
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.concatWithSlash
import com.verygoodsecurity.vgsshow.util.extension.logRequest
import com.verygoodsecurity.vgsshow.util.extension.logResponse
import com.verygoodsecurity.vgsshow.util.extension.toURL
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient as OkHttp3Client

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
internal class OkHttpClient : IHttpClient {

    private val cnameInterceptor: CnameInterceptor by lazy { CnameInterceptor() }

    private val client: OkHttp3Client by lazy {
        OkHttp3Client().newBuilder()
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(cnameInterceptor)
            .callTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }

    override fun execute(request: HttpRequest): HttpResponse {
        return client.newCall(buildOkHttpRequest(request)).execute().toHttpResponse()
    }

    override fun enqueue(request: HttpRequest, callback: HttpRequestCallback) {
        try {
            client.newCall(buildOkHttpRequest(request)).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onResponse(response.toHttpResponse())
                }
            })
        } catch (e: Exception) {
            callback.onFailure(e)
        }
    }

    override fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit) {
        cnameInterceptor.setCname(vaultId, cname, cnameResult)
    }

    override fun cancelAll() {
        client.dispatcher.cancelAll()
    }

    @Throws(Exception::class)
    private fun buildOkHttpRequest(request: HttpRequest): Request {
        return Request.Builder()
            .url((request.url concatWithSlash request.path).toURL())
            .addHeader(CONTENT_TYPE, request.format.toContentType())
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
            val requestId = UUID.randomUUID().toString()
            return chain.proceed(chain.request().also {
                it.logRequest(
                    requestId,
                    it.url.toString(),
                    it.method,
                    it.headers.toMap(),
                    getBody(it.body)
                )
            }).also {
                logResponse(
                    requestId,
                    it.request.url.toString(),
                    it.code,
                    it.message,
                    it.headers.toMap()
                )
            }
        }

        private fun getBody(request: RequestBody?): String {
            return try {
                val buffer = Buffer()
                request?.writeTo(buffer)
                buffer.readUtf8()
            } catch (e: IOException) {
                ""
            }
        }
    }
}