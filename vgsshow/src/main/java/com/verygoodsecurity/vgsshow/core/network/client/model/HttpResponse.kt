package com.verygoodsecurity.vgsshow.core.network.client.model

/**
 * Represents an HTTP response.
 * @suppress Not for public use.
 *
 * @property code The HTTP status code.
 * @property isSuccessful Whether the request was successful.
 * @property message The response message.
 * @property responseBody The response body.
 */
internal data class HttpResponse(
    val code: Int,
    val isSuccessful: Boolean,
    val message: String? = null,
    val responseBody: String? = null
)