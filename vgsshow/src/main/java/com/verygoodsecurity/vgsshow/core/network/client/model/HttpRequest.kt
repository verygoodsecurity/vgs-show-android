package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.Method

data class HttpRequest(
    val path: String,
    val method: Method,
    var headers: Map<String, String>? = null,
    var data: String? = null,
)