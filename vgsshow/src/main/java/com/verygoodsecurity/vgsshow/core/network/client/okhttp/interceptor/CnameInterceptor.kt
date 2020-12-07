package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class CnameInterceptor constructor() : Interceptor {

    private var vaultId: String? = null
    private var cname: String? = null

    fun setCname(vaultId: String, cname: String?) {
        this.vaultId = vaultId
        this.cname = cname
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(with(chain.request()) {
            if (!vaultId.isNullOrEmpty() && !cname.isNullOrEmpty()) {
                validateCname(chain, this)
            } else {
                this
            }
        })
    }

    private fun validateCname(chain: Interceptor.Chain, request: Request): Request {
        val r = request.newBuilder().get().url("<BUILD URl>")

        return request
    }
}