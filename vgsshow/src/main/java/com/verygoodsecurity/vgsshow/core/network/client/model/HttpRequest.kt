package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.data.request.RequestData

internal data class HttpRequest(
    val url: String,
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>?,
    var data: RequestData?,
    val format: VGSHttpBodyFormat,
    val requestTimeoutInterval: Long
)