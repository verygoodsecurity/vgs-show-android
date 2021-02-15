package com.verygoodsecurity.vgsshow.core.network.client.extension

import okhttp3.Request

internal fun Request.Builder.addHeaders(headers: Map<String, String>?): Request.Builder {
    headers?.forEach {
        addHeader(it.key, it.value)
    }
    return this
}