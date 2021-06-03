package com.verygoodsecurity.vgsshow.core.network.client.httpurl

import com.verygoodsecurity.vgsshow.core.network.client.*
import com.verygoodsecurity.vgsshow.core.network.client.extension.*
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.util.extension.*
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureTimeMillis

internal class HttpUrlClient constructor(isLogsEnabled: Boolean) : BaseHttpClient(isLogsEnabled) {

    private var cname: String? = null
    private var vaultId: String? = null
    private var isCnameValid: Boolean? = null
    private var cnameResult: ((Boolean, Long) -> Unit)? = null

    private val submittedTasks = mutableListOf<Future<*>>()

    private val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    override fun execute(request: HttpRequest): HttpResponse {
        val requestId: String = UUID.randomUUID().toString()
        var connection: HttpURLConnection? = null
        try {
            val requestHeaders =
                request.headers.plusItem(CONTENT_TYPE to request.format.toContentType())
            connection = (generateBaseUrl(request) concatWithSlash request.path).toURL().toString()
                .openConnection()
                .setSSLSocketFactory(TLSSocketFactory())
                .callTimeout(request.requestTimeoutInterval)
                .readTimeout(request.requestTimeoutInterval)
                .setInstanceFollowRedirectEnabled(false)
                .setIsUserInteractionEnabled(false)
                .setCacheEnabled(false)
                .addHeaders(requestHeaders)
                .setMethod(request.method)
            logRequestIfNeeded(requestId, request, requestHeaders, connection)
            writeData(connection, request.data?.getData())
            logResponseIfNeeded(requestId, connection)
            return readResponse(connection)
        } catch (e: Exception) {
            logExceptionIfNeeded(e)
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

    override fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit) {
        this.cname = cname
        this.vaultId = vaultId
        this.isCnameValid = null
        this.cnameResult = cnameResult
    }

    override fun cancelAll() {
        submittedTasks.forEach {
            it.cancel(true)
        }
        submittedTasks.clear()
    }

    private fun generateBaseUrl(request: HttpRequest): String {
        if (!cname.isNullOrEmpty() && !vaultId.isNullOrEmpty()) {
            return generateBaseUrlWithCname(request.url, cname!!, vaultId!!)
        }
        return request.url
    }

    private fun generateBaseUrlWithCname(
        url: String,
        cname: String,
        vaultId: String
    ): String = synchronized(this) {
        val isValid = isCnameValid ?: getValidatedCname(cname, vaultId).run {
            (this != null).also {
                isCnameValid = it
            }
        }
        return@synchronized if (isValid) cname.concatWithHttpProtocol() else url
    }

    private fun getValidatedCname(cname: String, vaultId: String): String? {
        var connection: HttpURLConnection? = null
        var responseTime: Long? = null
        var response: HttpResponse?
        return try {
            responseTime = measureTimeMillis {
                connection = cname.toHostnameValidationUrl(vaultId).openConnection()
                connection!!.requestMethod = "GET" // TODO: Refactor
                response = readResponse(connection!!)
            }
            response?.takeIf { it.isSuccessful }?.responseBody
                ?.takeIf { it.isNotEmpty() && it equalsHosts cname }
                ?.run {
                    cnameResult?.invoke(true, responseTime)
                    cname
                } ?: throw Exception()
        } catch (e: Exception) {
            logWaring("A specified cname($cname) incorrect, response time = $responseTime")
            cnameResult?.invoke(false, responseTime ?: 0)
            null
        } finally {
            connection?.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun writeData(connection: HttpURLConnection, data: ByteArray?) {
        data?.let {
            connection.outputStream.use { os ->
                os.write(it)
            }
        }
    }

    @Throws(IOException::class)
    private fun readResponse(connection: HttpURLConnection): HttpResponse {
        return when {
            connection.isSuccessful() -> connection.inputStream.bufferedReader().use {
                HttpResponse(connection.responseCode, true, responseBody = it.readText())
            }
            else -> connection.errorStream.bufferedReader().use {
                HttpResponse(connection.responseCode, false, message = it.readText())
            }
        }
    }

    private fun logRequestIfNeeded(
        requestId: String,
        request: HttpRequest,
        requestHeaders: Map<String, String>,
        connection: HttpURLConnection
    ) {
        if (!isLogsEnabled) {
            return
        }
        logRequest(
            requestId,
            connection.url.toString(),
            connection.requestMethod,
            requestHeaders,
            request.data?.getRawData()
        )
    }

    private fun logResponseIfNeeded(requestId: String, connection: HttpURLConnection) {
        if (!isLogsEnabled) {
            return
        }
        logResponse(
            requestId,
            connection.url.toString(),
            connection.responseCode,
            connection.responseMessage,
            connection.getHeaders()
        )
    }

    private fun logExceptionIfNeeded(e: Exception) {
        if (!isLogsEnabled) {
            return
        }
        logException(e)
    }
}