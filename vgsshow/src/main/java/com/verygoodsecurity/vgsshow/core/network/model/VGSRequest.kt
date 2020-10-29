package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.HttpMethod
import org.json.JSONObject

class VGSRequest private constructor(
    val path: String,
    val method: HttpMethod,
    var headers: Map<String, String>? = null,
    var payload: JSONObject? = null,
) {

    data class Builder(
        private val path: String,
        private val method: HttpMethod,
        private var headers: Map<String, String>? = null,
        private var payload:JSONObject? = null
    ) {

        fun headers(headers: Map<String, String>?) = apply { this.headers = headers }

        fun body(payload: JSONObject?) = apply { this.payload = payload }

        fun build() = VGSRequest(path, method, headers, payload)
    }
}