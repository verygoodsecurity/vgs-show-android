package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.toJSON
import okhttp3.Response

internal fun VGSRequest.toHttpRequest() = HttpRequest(
    this.path,
    this.method,
    this.headers,
    this.data?.toJSON()?.toString()
)

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

internal fun VGSException.toVGSResponse() = VGSResponse.Error(this)