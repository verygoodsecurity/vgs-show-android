package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.extension.equalsUrl
import com.verygoodsecurity.vgsshow.core.network.client.extension.toHost
import com.verygoodsecurity.vgsshow.core.network.client.extension.toHostnameValidationUrl
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class CnameInterceptor : Interceptor {

    private var vaultId: String? = null
    private var cname: String? = null

    fun setCname(vaultId: String, cname: String?) {
        this.vaultId = vaultId
        this.cname = cname
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(with(chain.request()) {
            if (isValidParams()) {
                validateCname(chain, this, cname!!, vaultId!!)
            } else {
                this
            }
        })
    }

    private fun isValidParams() = !vaultId.isNullOrEmpty() && !cname.isNullOrEmpty()

    private fun validateCname(
        chain: Interceptor.Chain,
        request: Request,
        cname: String,
        vaultId: String
    ): Request {
        val cnameRequest = request.newBuilder()
            .get()
            .url(cname.toHostnameValidationUrl(vaultId))
            .build()
        return try {
            chain.proceed(cnameRequest).takeIf { it.isSuccessful }?.let {
                val body = it.body?.string()
                return if (it.isSuccessful && !body.isNullOrEmpty() && body equalsUrl cname) {
                    request.newBuilder()
                        .url(
                            request.url.newBuilder()
                                .scheme(request.url.scheme)
                                .host(cname.toHost())
                                .build()
                        )
                        .build()
                } else {
                    logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
                    request
                }
            } ?: request
        } catch (e: Exception) {
            logDebug("A specified cname incorrect!", VGSShow::class.simpleName)
            request
        }
    }
}