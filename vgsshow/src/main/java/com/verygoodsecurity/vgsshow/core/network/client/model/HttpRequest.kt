package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.data.request.RequestData

internal data class HttpRequest(
    val url: String,
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>? = null,
    var data: RequestData? = null,
    val format: VGSHttpBodyFormat,
    val requestTimeoutInterval: Long
)