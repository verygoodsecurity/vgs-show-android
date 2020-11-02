package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.plus
import com.verygoodsecurity.vgsshow.util.extension.toMap
import okhttp3.Response
import org.json.JSONObject

internal fun VGSRequest.toHttpRequest(
    extraHeaders: Map<String, String>,
    extraData: Map<String, Any>
) = HttpRequest(
    this.path,
    this.method,
    extraHeaders + this.headers,
    (this.payload ?: if (extraData.isEmpty()) null else JSONObject())?.apply {
        extraData.forEach { (k, v) ->
            this.putOpt(k, v)
        }
    }?.toString()
)

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

internal fun HttpResponse.toVGSResponse() = if (isSuccessful) {
    VGSResponse.Success(code, responseBody?.toMap(), responseBody)
} else {
    VGSResponse.Error(VGSException.Exception(code, message))
}

internal fun VGSException.toVGSResponse() = VGSResponse.Error(this)