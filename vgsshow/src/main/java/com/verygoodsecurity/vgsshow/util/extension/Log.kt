package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.logs.VGSLogger

internal fun Any.logDebug(message: String, tag: String? = null) {
    VGSLogger.debug((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logWaring(message: String, tag: String? = null) {
    VGSLogger.warning((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logRequest(
    requestId: String,
    requestUrl: String,
    method: String,
    headers: Map<String, String>,
    payload: String?,
    tag: String? = null
) {
    VGSLogger.debug(
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
    VGSLogger.debug(
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