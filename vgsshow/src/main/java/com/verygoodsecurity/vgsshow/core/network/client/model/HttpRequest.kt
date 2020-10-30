package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.HttpMethod

internal data class HttpRequest(
    val path: String,
    val method: HttpMethod,
    var headers: Map<String, String>? = null,
    var data: String? = null,
)