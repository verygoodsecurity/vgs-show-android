package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod

internal data class HttpRequest(
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>? = null,
    var data: String? = null,
    val format: VGSHttpBodyFormat
)