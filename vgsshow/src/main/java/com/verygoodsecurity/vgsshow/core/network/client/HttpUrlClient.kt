package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.*
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequestCallback
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.util.extension.concatWithSlash
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import java.io.IOException
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class HttpUrlClient constructor(private val baseUrl: String) : IHttpClient {

    private val submittedTasks = mutableListOf<Future<*>>()

    private val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    override fun execute(request: HttpRequest): HttpResponse {
        var connection: HttpURLConnection? = null
        try {
            connection = (baseUrl concatWithSlash request.path).openConnection()
                .setSSLSocketFactory(TLSSocketFactory())
                .callTimeout(CONNECTION_TIME_OUT)
                .readTimeout(CONNECTION_TIME_OUT)
                .setInstanceFollowRedirectEnabled(false)
                .setIsUserInteractionEnabled(false)
                .setCacheEnabled(false)
                .addHeader(CONTENT_TYPE, request.format.toContentType())
                .addHeaders(request.headers)
                .setMethod(request.method)

            logDebug("Request{method=${connection.requestMethod}}", VGSShow::class.simpleName)
            writeData(connection, request.data)

            logDebug(
                "Response{code=${connection.responseCode}, message=${connection.responseMessage}}",
                VGSShow::class.simpleName
            )
            return readResponse(connection)
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    override fun enqueue(request: HttpRequest, callback: HttpRequestCallback) {
        var task: Future<*>? = null
        task = executor.submit {
            try {
                callback.onResponse(this@HttpUrlClient.execute(request))
                submittedTasks.remove(task)
            } catch (e: Exception) {
                callback.onFailure(e)
            }
        }
        submittedTasks.add(task)
    }

    override fun cancelAll() {
        submittedTasks.forEach {
            it.cancel(true)
        }
        submittedTasks.clear()
    }

    @Throws(IOException::class)
    private fun writeData(connection: HttpURLConnection, data: String?) {
        data?.toByteArray(Charsets.UTF_8).let {
            connection.outputStream.use { os ->
                os.write(it)
            }
        }
    }

    @Throws(IOException::class)
    private fun readResponse(connection: HttpURLConnection): HttpResponse {
        return when (val responseCode = connection.responseCode) {
            HTTP_OK -> connection.inputStream.bufferedReader().use {
                HttpResponse(responseCode, true, responseBody = it.readText())
            }
            else -> connection.errorStream.bufferedReader().use {
                HttpResponse(responseCode, false, message = it.readText())
            }
        }
    }
}