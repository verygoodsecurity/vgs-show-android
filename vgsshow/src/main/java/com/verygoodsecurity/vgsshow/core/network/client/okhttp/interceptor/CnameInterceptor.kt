package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import com.verygoodsecurity.vgsshow.core.network.client.extension.toHostnameValidationUrl
import com.verygoodsecurity.vgsshow.util.extension.equalsHosts
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.util.extension.toHost
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import kotlin.system.measureTimeMillis

/**
 * An OkHttp interceptor for handling CNAME resolution.
 * @suppress Not for public use.
 */
internal class CnameInterceptor : Interceptor {

    private var cname: String? = null
    private var vaultId: String? = null
    private var isCnameValid: Boolean? = null
    private var cnameResult: ((Boolean, Long) -> Unit)? = null

    /**
     * Sets the CNAME and vault ID for the interceptor.
     *
     * @param vaultId The ID of the vault.
     * @param cname The CNAME to use.
     * @param cnameResult A callback to be invoked with the result of the CNAME validation.
     * The callback receives a boolean indicating success and a long for the latency.
     */
    fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit) {
        this.cname = cname
        this.vaultId = vaultId
        this.isCnameValid = null
        this.cnameResult = cnameResult
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = with(chain.request()) {
            if (!cname.isNullOrEmpty() && !vaultId.isNullOrEmpty()) {
                return@with buildRequestWithCname(chain, this, cname!!, vaultId!!)
            }
            this
        }
        return chain.proceed(request)
    }

    private fun buildRequestWithCname(
        chain: Interceptor.Chain,
        request: Request,
        cname: String,
        vaultId: String
    ): Request {
        val isValid = isCnameValid ?: getValidatedCname(chain, request, cname, vaultId).run {
            (this != null).also {
                isCnameValid = it
            }
        }
        return request.newBuilder()
            .url(if (isValid) buildCnameUrl(request, cname) else request.url)
            .build()
    }

    private fun getValidatedCname(
        chain: Interceptor.Chain,
        request: Request,
        cname: String,
        vaultId: String
    ): String? {
        val cnameRequest = buildCnameRequest(request, cname, vaultId)
        var response: Response? = null
        var responseTime: Long? = null
        return try {
            responseTime = measureTimeMillis {
                response = chain.proceed(cnameRequest)
            }
            response?.takeIf { it.isSuccessful }?.body?.string()
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
            response?.close()
        }
    }

    private fun buildCnameRequest(request: Request, cname: String, vaultId: String): Request =
        request.newBuilder()
            .get()
            .url(cname.toHostnameValidationUrl(vaultId))
            .build()

    private fun buildCnameUrl(request: Request, cname: String): HttpUrl =
        request.url.newBuilder()
            .scheme(request.url.scheme)
            .host(cname.toHost())
            .build()
}