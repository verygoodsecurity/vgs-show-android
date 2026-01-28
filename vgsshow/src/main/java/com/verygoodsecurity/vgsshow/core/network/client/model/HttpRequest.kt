package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.data.request.RequestData

/**
 * Represents an HTTP request.
 * @suppress Not for public use.
 *
 * @property url The URL for the request.
 * @property path The path for the request.
 * @property method The HTTP method for the request.
 * @property headers The headers for the request.
 * @property data The data to be sent with the request.
 * @property format The format of the request body.
 * @property requestTimeoutInterval The timeout interval for the request, in milliseconds.
 */
internal data class HttpRequest(
    val url: String,
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>?,
    var data: RequestData?,
    val format: VGSHttpBodyFormat,
    val requestTimeoutInterval: Long
)