package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import org.json.JSONObject

class VGSRequest private constructor(
    val path: String,
    val method: VGSHttpMethod,
    val headers: Map<String, String>? = null,
    val payload: JSONObject? = null,
    val requestFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON,
    val responseFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
) {

    data class Builder(
        private val path: String,
        private val method: VGSHttpMethod,
        private var headers: Map<String, String>? = null,
        private var payload: JSONObject? = null,
        private var requestFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON,
        private var responseFormat: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON
    ) {

        fun headers(headers: Map<String, String>) = apply { this.headers = headers }

        // TODO: Currently it always JSONObject but in future it will change to support xml
        fun body(payload: JSONObject, format: VGSHttpBodyFormat = VGSHttpBodyFormat.JSON): Builder {
            return apply {
                this.payload = payload
                this.requestFormat = format
            }
        }

        fun responseFormat(format: VGSHttpBodyFormat) = apply { this.responseFormat = format }

        fun build() = VGSRequest(path, method, headers, payload, requestFormat, responseFormat)
    }
}