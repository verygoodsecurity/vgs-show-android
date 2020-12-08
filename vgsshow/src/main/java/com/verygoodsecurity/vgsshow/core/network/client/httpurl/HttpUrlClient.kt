package com.verygoodsecurity.vgsshow.core.network.client.httpurl

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.*
import com.verygoodsecurity.vgsshow.core.network.client.extension.*
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.extension.toContentType
import com.verygoodsecurity.vgsshow.util.extension.*
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class HttpUrlClient constructor(private val baseUrl: String) : IHttpClient {

    private var cname: String? = null
    private var vaultId: String? = null
    private var isCnameValid: Boolean? = null

    private val submittedTasks = mutableListOf<Future<*>>()

    private val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    override fun execute(request: HttpRequest): HttpResponse {
        var connection: HttpURLConnection? = null
        try {
            connection = (generateBaseUrl() concatWithSlash request.path).openConnection()
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

    override fun setCname(vaultId: String, cname: String?) {
        this.cname = cname
        this.vaultId = vaultId
        this.isCnameValid = null
    }

    override fun cancelAll() {
        submittedTasks.forEach {
            it.cancel(true)
        }
        submittedTasks.clear()
    }

    private fun generateBaseUrl(): String {
        if (!cname.isNullOrEmpty() && !vaultId.isNullOrEmpty()) {
            return generateBaseUrlWithCname(cname!!, vaultId!!)
        }
        return baseUrl
    }

    private fun generateBaseUrlWithCname(cname: String, vaultId: String): String =
        synchronized(this) {
            return when (isCnameValid) {
                true -> cname.concatWithHttpProtocol()
                false -> baseUrl
                else -> {
                    isCnameValid = getValidatedCname(cname, vaultId) != null
                    if (isCnameValid == true) cname.toHost().concatWithHttpProtocol() else baseUrl
                }
            }
        }

    private fun getValidatedCname(cname: String, vaultId: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            connection = cname.toHostnameValidationUrl(vaultId).openConnection()
            connection.requestMethod = "GET" // TODO: Refactor
            val response = readResponse(connection)
            val responseCname = response.responseBody?.toHost()
            if (response.isSuccessful && !responseCname.isNullOrEmpty() && responseCname equalsHosts cname) {
                logDebug("Specified cname valid: $cname", VGSShow::class.simpleName)
                cname
            } else {
                logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
                null
            }
        } catch (e: Exception) {
            logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
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
}