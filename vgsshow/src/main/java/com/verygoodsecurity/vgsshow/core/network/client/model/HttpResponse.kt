package com.verygoodsecurity.vgsshow.core.network.client.model

data class HttpResponse(
    val code: Int,
    val isSuccessful: Boolean,
    val message: String?,
    val responseBody: String?
)