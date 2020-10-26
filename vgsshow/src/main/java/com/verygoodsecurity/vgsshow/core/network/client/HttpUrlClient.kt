package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

internal class HttpUrlClient constructor(
    private val baseUrl: String? = null
) : IHttpClient {

    override fun call(request: HttpRequest): HttpResponse {
        return HttpResponse(-1, false, "test", "test")
    }
}