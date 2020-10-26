package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.util.extension.with
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class HttpUrlClient constructor(private val baseUrl: String) : IHttpClient {

    override fun call(request: HttpRequest): HttpResponse {
        var connection: HttpURLConnection? = null
        try {
            connection = (baseUrl with request.path).openConnection()
            baseSetup(connection)
            return HttpResponse(-1, false, "test", "test")
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    @Throws(ClassCastException::class)
    private fun String.openConnection() = (URL(this).openConnection() as HttpURLConnection)

    private fun baseSetup(connection: HttpURLConnection) {
        with(connection) {
            (this as? HttpsURLConnection)?.sslSocketFactory = TLSSocketFactory()
            connectTimeout = CONNECTION_TIME_OUT.toInt()
            readTimeout = CONNECTION_TIME_OUT.toInt()
            instanceFollowRedirects = false
            allowUserInteraction = false
            useCaches = false
        }
    }
}