package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.extension.toJsonByteArray

class VGSRequest private constructor(
    val path: String,
    val method: VGSHttpMethod,
    val headers: Map<String, String>? = null,
    val payload: ByteArray? = null,
    val requestFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON,
    val responseFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
) {

    /**
     * VGSRequest builder helper.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     */
    data class Builder(private val path: String, private val method: VGSHttpMethod) {

        private var headers: Map<String, String>? = null
        private var payload: ByteArray? = null
        private var requestFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
        private var responseFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON

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
        fun body(payload: Map<String, Any>): Builder = apply {
            this.payload = payload.toJsonByteArray()
        }

        // TODO: Uncomment when public release needed
//        /**
//         * Body that will send with this request.
//         *
//         */
//        fun body(payload: String, requestFormat: VGSHttpBodyFormat): Builder = apply {
//            this.payload = payload.toByteArray(Charsets.UTF_8)
//            this.requestFormat = requestFormat
//        }
//
        // TODO: Uncomment when public release needed
//        /**
//         * Body that will send with this request.
//         *
//         */
//        fun body(payload: ByteArray, requestFormat: VGSHttpBodyFormat): Builder = apply {
//            this.payload = payload
//            this.requestFormat = requestFormat
//        }

        /**
         * Specifies expected response body format.
         *
         * @param format @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat]
         */
        fun responseFormat(format: VGSHttpBodyFormat) = apply { this.responseFormat = format }

        /**
         * Build VGSRequest object.
         *
         * @return configured VGSRequest.
         */
        fun build() = VGSRequest(path, method, headers, payload, requestFormat, responseFormat)
    }
}