package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.plus
import okhttp3.Response
import org.json.JSONObject

internal fun VGSRequest.toHttpRequest(url: String, extraHeaders: Map<String, String>?) =
    HttpRequest(
        url,
        this.path,
        this.method,
        this.headers + extraHeaders,
        this.payload,
        this.requestFormat,
        this.requestTimeoutInterval
    )

private const val APPLICATION_JSON = "application/json"
private const val APPLICATION_URLENCODED = "application/x-www-form-urlencoded"

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.JSON -> APPLICATION_JSON
    VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> APPLICATION_URLENCODED
}

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

internal fun VGSException.toVGSResponse() = VGSResponse.Error.create(this)

internal fun String.toJsonOrNull(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}

internal fun Map<String, Any>.toJsonOrNull(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}