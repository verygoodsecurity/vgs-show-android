package com.verygoodsecurity.vgsshow.core.network.client.okhttp.interceptor

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import okhttp3.Interceptor
import okhttp3.Response

internal class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        with(VGSShow::class.java.simpleName) {
            return chain.proceed(chain.request().also {
                logDebug("Request{method=${it.method}}", VGSShow::class.simpleName)
            }).also {
                logDebug(
                    "Response{code=${it.code}, message=${it.message}}",
                    VGSShow::class.simpleName
                )
            }
        }
    }
}
