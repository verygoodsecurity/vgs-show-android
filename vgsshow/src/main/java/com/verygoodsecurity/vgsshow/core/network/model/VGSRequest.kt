package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.data.request.JsonRequestData
import com.verygoodsecurity.vgsshow.core.network.model.data.request.RequestData
import com.verygoodsecurity.vgsshow.core.network.model.data.request.UrlencodedData

private const val DEFAULT_CONNECTION_TIME_OUT = 60000L

/**
 * Represents a request to the VGS proxy.
 *
 * @property routeId The route ID for the request.
 * @property path The path for the request.
 * @property method The HTTP method for the request.
 * @property headers The headers for the request.
 * @property requestFormat The format of the request body.
 * @property responseFormat The expected format of the response body.
 * @property requestTimeoutInterval The timeout interval for the request, in milliseconds.
 * @property payload The payload for the request.
 */
class VGSRequest private constructor(
    val routeId: String?,
    val path: String,
    val method: VGSHttpMethod,
    val headers: Map<String, String>?,
    val requestFormat: VGSHttpBodyFormat,
    val responseFormat: VGSHttpBodyFormat,
    val requestTimeoutInterval: Long,
    internal val payload: RequestData?,
) {

    /**
     * Returns `true` if the request payload is valid, `false` otherwise.
     */
    fun isPayloadValid(): Boolean = payload?.isValid() == true

    /**
     * A builder for creating [VGSRequest]s.
     *
     * @param path The path for the request.
     * @param method The HTTP method for the request.
     */
    data class Builder(
        private val path: String,
        private val method: VGSHttpMethod
    ) {

        private var routeId: String? = null
        private var headers: Map<String, String>? = null
        private var requestFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
        private var responseFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
        private var payload: RequestData? = null
        private var requestTimeoutInterval: Long = DEFAULT_CONNECTION_TIME_OUT

        /**
         * Sets the route ID for the request.
         *
         * @param id The route ID.
         * @return This builder.
         */
        fun routeId(id: String?) = apply { this.routeId = id }

        /**
         * Sets the headers for the request.
         *
         * @param headers The headers.
         * @return This builder.
         */
        fun headers(headers: Map<String, String>) = apply { this.headers = headers }

        /**
         * Sets the body of the request as a [Map].
         *
         * @param payload The request body.
         * @return This builder.
         */
        fun body(payload: Map<String, Any>?): Builder = apply {
            this.payload = if (payload != null) JsonRequestData(payload) else null
        }

        /**
         * Sets the body of the request as a [String] with the specified format.
         *
         * @param payload The request body.
         * @param format The format of the request body.
         * @return This builder.
         */
        fun body(payload: String, format: VGSHttpBodyFormat): Builder = apply {
            this.requestFormat = format
            this.responseFormat = format
            this.payload = when (format) {
                VGSHttpBodyFormat.JSON -> JsonRequestData(payload)
                VGSHttpBodyFormat.X_WWW_FORM_URLENCODED -> UrlencodedData(payload)
            }
        }

        /**
         * Sets the expected response body format.
         *
         * @param format The response body format.
         * @return This builder.
         */
        fun responseFormat(format: VGSHttpBodyFormat) = apply { this.responseFormat = format }

        /**
         * Sets the request timeout interval.
         *
         * @param timeout The timeout interval, in milliseconds.
         * @return This builder.
         */
        fun requestTimeoutInterval(timeout: Long) = apply { this.requestTimeoutInterval = timeout }

        /**
         * Builds the [VGSRequest].
         *
         * @return The built [VGSRequest].
         */
        fun build() = VGSRequest(
            routeId,
            path,
            method,
            headers,
            requestFormat,
            responseFormat,
            requestTimeoutInterval,
            payload
        )
    }
}