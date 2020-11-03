package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import org.json.JSONObject

class VGSRequest private constructor(
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>? = null,
    var payload: JSONObject? = null,
    var format: VGSHttpFormat
) {

    data class Builder(
        private val path: String,
        private val method: VGSHttpMethod,
        private var headers: Map<String, String>? = null,
        private var payload: JSONObject? = null,
        private var format: VGSHttpFormat = VGSHttpFormat.JSON
    ) {

        fun headers(headers: Map<String, String>) = apply { this.headers = headers }

        // TODO: Currently it always JSONObject but in future it will change to support xml
        fun body(payload: JSONObject, format: VGSHttpFormat = VGSHttpFormat.JSON): Builder {
            return apply {
                this.payload = payload
                this.format = format
            }
        }

        fun build() = VGSRequest(path, method, headers, payload, format)
    }
}