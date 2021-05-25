package com.verygoodsecurity.vgsshow.core.network.client

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

internal const val CONTENT_TYPE = "Content-type"

internal interface IHttpClient {

    @WorkerThread
    @Throws(Exception::class)
    fun execute(request: HttpRequest): HttpResponse

    fun enqueue(request: HttpRequest, callback: HttpRequestCallback)

    fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit)

    fun cancelAll()
}