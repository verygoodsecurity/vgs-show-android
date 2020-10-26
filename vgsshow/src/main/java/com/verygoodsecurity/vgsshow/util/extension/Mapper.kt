package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import okhttp3.Response

internal fun VGSRequest.toHttpRequest() = HttpRequest(
    this.path,
    this.method,
    this.headers,
    this.data?.mapToJSON()?.toString()
)

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)