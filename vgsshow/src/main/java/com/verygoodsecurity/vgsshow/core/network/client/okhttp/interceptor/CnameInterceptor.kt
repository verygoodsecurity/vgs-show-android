package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.equalsUrl
import com.verygoodsecurity.vgsshow.core.network.client.extension.toHost
import com.verygoodsecurity.vgsshow.core.network.client.extension.toHostnameValidationUrl
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class CnameInterceptor : Interceptor {

    private var vaultId: String? = null
    private var cname: String? = null

    private var isCnameValid: Boolean? = null

    fun setCname(vaultId: String, cname: String?) {
        this.vaultId = vaultId
        this.cname = cname
        this.isCnameValid = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(with(chain.request()) {
            if (!vaultId.isNullOrEmpty() && !cname.isNullOrEmpty()) {
                return@with buildRequestWithCname(chain, this, cname!!, vaultId!!)
            }
            this
        })
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
        return try {
            chain.proceed(cnameRequest).takeIf { it.isSuccessful }?.let { response ->
                val body = response.body?.string()
                if (!body.isNullOrEmpty() && body equalsUrl cname) {
                    logDebug("Specified cname valid: $cname", VGSShow::class.simpleName)
                    cname
                } else {
                    logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
                    null
                }
            }
        } catch (e: Exception) {
            logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
            null
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