package com.verygoodsecurity.vgsshow.core.network.client.model

internal interface HttpRequestCallback {

    fun onResponse(response: HttpResponse)

    fun onFailure(e: Exception)
}