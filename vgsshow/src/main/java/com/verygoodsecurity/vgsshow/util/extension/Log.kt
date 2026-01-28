package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.logs.VGSShowLogger
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 * Logs a debug message.
 * @suppress Not for public use.
 *
 * @param message The message to log.
 * @param tag The tag to use for the log message.
 */
internal fun Any.logDebug(message: String, tag: String? = null) {
    VGSShowLogger.debug((tag ?: this::class.java.simpleName), message)
}

/**
 * Logs a warning message.
 * @suppress Not for public use.
 *
 * @param message The message to log.
 * @param tag The tag to use for the log message.
 */
internal fun Any.logWaring(message: String, tag: String? = null) {
    VGSShowLogger.warning((tag ?: this::class.java.simpleName), message)
}

/**
 * Logs an exception.
 * @suppress Not for public use.
 *
 * @param e The exception to log.
 * @param tag The tag to use for the log message.
 */
internal fun Any.logException(e: Exception, tag: String? = null) {
    VGSShowLogger.warning((tag ?: this::class.java.simpleName), "e: ${e::class.java}, message: ${e.message}")
}

/**
 * Logs a request.
 * @suppress Not for public use.
 *
 * @param requestId The ID of the request.
 * @param requestUrl The URL of the request.
 * @param method The HTTP method of the request.
 * @param headers The headers of the request.
 * @param payload The payload of the request.
 * @param tag The tag to use for the log message.
 */
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

/**
 * Logs a response.
 * @suppress Not for public use.
 *
 * @param requestId The ID of the request.
 * @param requestUrl The URL of the request.
 * @param responseCode The HTTP response code.
 * @param responseMessage The HTTP response message.
 * @param headers The headers of the response.
 * @param tag The tag to use for the log message.
 */
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

/**
 * Logs the start of the views update.
 * @suppress Not for public use.
 *
 * @param views The set of views being updated.
 */
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