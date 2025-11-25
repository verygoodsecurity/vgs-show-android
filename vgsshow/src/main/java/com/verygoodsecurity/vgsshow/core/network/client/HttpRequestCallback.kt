package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

/**
 * A callback interface for HTTP requests.
 * @suppress Not for public use.
 */
internal interface HttpRequestCallback {

    /**
     * Called when a response is received.
     *
     * @param response The HTTP response.
     */
    fun onResponse(response: HttpResponse)

    /**
     * Called when an error occurs.
     *
     * @param e The exception that occurred.
     */
    fun onFailure(e: Exception)
}