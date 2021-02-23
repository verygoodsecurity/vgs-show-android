package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.logs.VGSShowLogger
import com.verygoodsecurity.vgsshow.widget.core.VGSView

internal fun Any.logDebug(message: String, tag: String? = null) {
    VGSShowLogger.debug((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logWaring(message: String, tag: String? = null) {
    VGSShowLogger.warning((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logException(e: Exception, tag: String? = null) {
    VGSShowLogger.warning((tag ?: this::class.java.simpleName), "e: ${e::class.java}, message: ${e.message}")
}

internal fun Any.logRequest(
    requestId: String,
    requestUrl: String,
    method: String,
    headers: Map<String, String>,
    payload: String?,
    tag: String? = null
) {
    VGSShowLogger.debug(
        (tag ?: this::class.java.simpleName),
        """
            --> Send VGSShowSDK request id: $requestId
            --> Send VGSShowSDK request url: $requestUrl
            --> Send VGSShowSDK method: $method
            --> Send VGSShowSDK request headers: $headers
            --> Send VGSShowSDK request payload: $payload
        """
    )
}

internal fun Any.logResponse(
    requestId: String,
    requestUrl: String,
    responseCode: Int,
    responseMessage: String,
    headers: Map<String, String>,
    tag: String? = null
) {
    VGSShowLogger.debug(
        (tag ?: this::class.java.simpleName),
        """
            <-- VGSShowSDK request id: $requestId
            <-- VGSShowSDK request url: $requestUrl
            <-- VGSShowSDK response code: $responseCode
            <-- VGSShowSDK response message: $responseMessage
            <-- VGSShowSDK response headers: $headers
        """
    )
}

internal fun Any.logStartViewsUpdate(views: Set<VGSView<*>>) {
    val contentPaths = views.map {
        with(it.getContentPath()) {
            if (isNullOrEmpty()) "CONTENT PATH NOT SET OR EMPTY" else this
        }
    }
    logDebug("Start decoding revealed data for contentPaths: $contentPaths")
    if (views.any { it.getContentPath().isEmpty() }) {
        logWaring("Some subscribed views seems to have empty content path. Verify `contentPath` property is set for each view.")
    }
}