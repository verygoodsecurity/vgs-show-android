package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

internal interface HttpRequestCallback {

    fun onResponse(response: HttpResponse)

    fun onFailure(e: Exception)
}