package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.util.extension.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK

internal class HttpUrlClient constructor(private val baseUrl: String) : IHttpClient {

    override fun call(request: HttpRequest): HttpResponse {
        var connection: HttpURLConnection? = null
        try {
            connection = (baseUrl with request.path).openConnection()
                .setSSLSocketFactory(TLSSocketFactory())
                .callTimeout(CONNECTION_TIME_OUT)
                .readTimeout(CONNECTION_TIME_OUT)
                .setInstanceFollowRedirectEnabled(false)
                .setIsUserInteractionEnabled(false)
                .setCacheEnabled(false)
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