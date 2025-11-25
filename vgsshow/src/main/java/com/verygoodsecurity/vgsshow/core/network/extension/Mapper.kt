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

/**
 * Converts a [VGSRequest] to an [HttpRequest].
 * @suppress Not for public use.
 */
internal fun VGSRequest.toHttpRequest(url: String, extraHeaders: Map<String, String>?) =
    HttpRequest(
        addRouteId(url, this.routeId),
        this.path,
        this.method,
        this.headers + extraHeaders,
        this.payload,
        this.requestFormat,
        this.requestTimeoutInterval
    )

private fun addRouteId(url: String, routeId: String?): String {
    val position = url.indexOf(".")
    return if (position < 0 || routeId.isNullOrEmpty()) {
        url
    } else {
        StringBuilder(url)
            .insert(position, routeId)
            .insert(position, "-")
            .toString()
    }
}

private const val APPLICATION_JSON = "application/json"
private const val APPLICATION_URLENCODED = "application/x-www-form-urlencoded"

/**
 * Converts a [VGSHttpBodyFormat] to a content type string.
 * @suppress Not for public use.
 */
internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.JSON -> APPLICATION_JSON
    VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> APPLICATION_URLENCODED
}

/**
 * Converts an OkHttp [Response] to an [HttpResponse].
 * @suppress Not for public use.
 */
internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

/**
 * Converts a [VGSException] to a [VGSResponse.Error].
 * @suppress Not for public use.
 */
internal fun VGSException.toVGSResponse() = VGSResponse.Error.create(this)

/**
 * Converts a string to a [JSONObject], or `null` if the string is not a valid JSON object.
 * @supprepss Not for public use.
 */
internal fun String.toJsonOrNull(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}

/**
 * Converts a map to a [JSONObject], or `null` if the map cannot be converted.
 * @suppress Not for public use.
 */
internal fun Map<String, Any>.toJsonOrNull(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: Exception) {
        null
    }
}