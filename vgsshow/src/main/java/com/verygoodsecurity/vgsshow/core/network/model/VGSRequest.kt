package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.data.request.JsonRequestData
import com.verygoodsecurity.vgsshow.core.network.model.data.request.RequestData
import com.verygoodsecurity.vgsshow.core.network.model.data.request.UrlencodedData

private const val DEFAULT_CONNECTION_TIME_OUT = 60000L

/**
 * Request definition class for revealing data.
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
     * Check if payload is valid
     *
     * @return true if payload valid
     */
    fun isPayloadValid(): Boolean = payload?.isValid() == true

    /**
     * VGSRequest builder helper.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
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
         * Defines route id for submitting data.
         *
         * @param id A vault route id
         */
        fun routeId(id: String?) = apply { this.routeId = id }

        /**
         * List of headers that will be added to this request.
         *
         * @param headers key-value headers store, where key - header name and value - header value.
         * (ex. key = "Authorization", value = "authorization_token")
         */
        fun headers(headers: Map<String, String>) = apply { this.headers = headers }

        /**
         * Body that will send with this request.
         *
         */
        fun body(payload: Map<String, Any>?): Builder = apply {
            this.payload = if (payload != null) JsonRequestData(payload) else null
        }

        /**
         * Body that will send with this request.
         *
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
         * Specifies expected response body format.
         *
         * @param format @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat]
         */
        fun responseFormat(format: VGSHttpBodyFormat) = apply { this.responseFormat = format }

        /**
         * Specifies request timeout interval in milliseconds.
         *
         * @param timeout interval in milliseconds.
         */
        fun requestTimeoutInterval(timeout: Long) = apply { this.requestTimeoutInterval = timeout }

        /**
         * Build VGSRequest object.
         *
         * @return configured VGSRequest.
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