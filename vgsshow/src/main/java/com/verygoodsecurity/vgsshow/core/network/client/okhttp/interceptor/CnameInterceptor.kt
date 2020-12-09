package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.toHostnameValidationUrl
import com.verygoodsecurity.vgsshow.util.extension.equalsHosts
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.toHost
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import kotlin.system.measureTimeMillis

internal class CnameInterceptor : Interceptor {

    private var cname: String? = null
    private var vaultId: String? = null
    private var isCnameValid: Boolean? = null
    private var cnameResult: ((Boolean, Long) -> Unit)? = null

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
        return request.newBuilder().url(
            when (isCnameValid) {
                true -> buildCnameUrl(request, cname)
                false -> request.url
                else -> {
                    isCnameValid = getValidatedCname(chain, request, cname, vaultId) != null
                    if (isCnameValid == true) buildCnameUrl(request, cname) else request.url
                }
            }
        ).build()
    }

    private fun getValidatedCname(
        chain: Interceptor.Chain,
        request: Request,
        cname: String,
        vaultId: String
    ): String? {
        val cnameRequest = buildCnameRequest(request, cname, vaultId)
        var response: Response? = null
        return try {
            val responseTime = measureTimeMillis {
                response = chain.proceed(cnameRequest)
            }
            val body = response?.body?.string()
            return if (!body.isNullOrEmpty() && body equalsHosts cname) {
                cnameResult?.invoke(true, responseTime)
                cname
            } else {
                logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
                cnameResult?.invoke(false, responseTime)
                null
            }
        } catch (e: Exception) {
            logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
            cnameResult?.invoke(false, 0)
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