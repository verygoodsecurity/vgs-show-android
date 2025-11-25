package com.verygoodsecurity.vgsshow.core.network.client.okhttp

import com.verygoodsecurity.vgsshow.core.network.client.BaseHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.CONTENT_TYPE
import com.verygoodsecurity.vgsshow.core.network.client.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.extension.addHeaders
import com.verygoodsecurity.vgsshow.core.network.client.extension.toRequestBodyOrNull
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor.CnameInterceptor
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.core.network.extension.toHttpResponse
import com.verygoodsecurity.vgsshow.util.extension.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient as OkHttp3Client

/**
 * An implementation of [BaseHttpClient] that uses OkHttp to make requests.
 * @suppress Not for public use.
 *
 * @param isLogsEnabled Whether or not to enable logging.
 */
internal class OkHttpClient constructor(isLogsEnabled: Boolean) : BaseHttpClient(isLogsEnabled) {

    private val cnameInterceptor: CnameInterceptor by lazy { CnameInterceptor() }

    private val client: OkHttp3Client by lazy {
        OkHttp3Client().newBuilder()
            .addInterceptor(cnameInterceptor)
            .also { if (isLogsEnabled) it.addInterceptor(LoggingInterceptor()) }
            .build()
    }

    override fun execute(request: HttpRequest): HttpResponse {
        return client.newBuilder()
            .callTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
            .readTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
            .writeTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
            .build()
            .newCall(buildOkHttpRequest(request)).execute().toHttpResponse()
    }

    override fun enqueue(request: HttpRequest, callback: HttpRequestCallback) {
        try {
            client.newBuilder()
                .callTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .readTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .writeTimeout(request.requestTimeoutInterval, TimeUnit.MILLISECONDS)
                .build()
                .newCall(buildOkHttpRequest(request)).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        logException(e)
                        callback.onFailure(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        callback.onResponse(response.toHttpResponse())
                    }
                })
        } catch (e: Exception) {
            logException(e)
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
        val contentType = request.format.toContentType()
        val mediaType = contentType.toMediaTypeOrNull()
        val requestBody = request.data?.getData().toRequestBodyOrNull(mediaType, request.method)
        return Request.Builder()
            .url((request.url concatWithSlash request.path).toURL())
            .addHeaders(request.headers.plusItem(CONTENT_TYPE to contentType))
            .method(request.method.name, requestBody)
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
                    getBody(it.body),
                    it::class.java.simpleName
                )
            }).also {
                logResponse(
                    requestId,
                    it.request.url.toString(),
                    it.code,
                    it.message,
                    it.headers.toMap(),
                    it::class.java.simpleName
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