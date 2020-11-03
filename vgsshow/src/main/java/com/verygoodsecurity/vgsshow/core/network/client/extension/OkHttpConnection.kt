package com.verygoodsecurity.vgsshow.core.network.client.extension

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST

internal fun Request.Builder.addHeaders(headers: Map<String, String>?): Request.Builder {
    headers?.forEach {
        addHeader(it.key, it.value)
    }
    return this
}

internal fun Request.Builder.setMethod(
    method: VGSHttpMethod,
    data: String?,
    mediaType: MediaType?
): Request.Builder {
    when (method) {
        VGSHttpMethod.POST -> post(data?.toRequestBody(mediaType) ?: EMPTY_REQUEST)
    }
    return this
}