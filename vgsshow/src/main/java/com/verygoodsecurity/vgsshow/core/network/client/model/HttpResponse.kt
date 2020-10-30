package com.verygoodsecurity.vgsshow.core.network.client.model

internal data class HttpResponse(
    val code: Int,
    val isSuccessful: Boolean,
    val message: String? = null,
    val responseBody: String? = null
)