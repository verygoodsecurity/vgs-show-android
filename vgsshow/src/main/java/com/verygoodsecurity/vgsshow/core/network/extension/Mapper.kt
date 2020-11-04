package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.plus
import okhttp3.Response

internal fun VGSRequest.toHttpRequest(extraHeaders: Map<String, String>) = HttpRequest(
    this.path,
    this.method,
    extraHeaders + this.headers, // TODO: Maybe it's better to generate VGSRequest already with all headers?
    this.payload?.toString(),
    this.requestFormat
)

private const val APPLICATION_JSON = "application/json"

internal fun VGSHttpFormat.toContentType() = when(this) {
    VGSHttpFormat.JSON -> APPLICATION_JSON
}

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

internal fun VGSException.toVGSResponse() = VGSResponse.Error(this)