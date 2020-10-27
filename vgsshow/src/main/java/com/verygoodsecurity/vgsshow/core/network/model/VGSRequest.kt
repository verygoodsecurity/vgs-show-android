package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.network.client.HttpMethod

class VGSRequest private constructor(
    val path: String,
    val method: HttpMethod,
    var headers: Map<String, String>? = null,
    var data: Map<String, Any>? = null,
) {

    data class Builder(
        private val path: String,
        private val method: HttpMethod,
        private var headers: Map<String, String>? = null,
        private var data: Map<String, Any>? = null
    ) {

        fun headers(headers: Map<String, String>?) = apply { this.headers = headers }

        fun body(data: Map<String, Any>?) = apply { this.data = data }

        fun build() = VGSRequest(path, method, headers, data)
    }
}