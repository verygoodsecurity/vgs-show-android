package com.verygoodsecurity.vgsshow.core.network.client.model

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod

internal data class HttpRequest(
    val path: String,
    val method: VGSHttpMethod,
    var headers: Map<String, String>? = null,
    var data: ByteArray? = null,
    val format: VGSHttpBodyFormat
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HttpRequest

        if (path != other.path) return false
        if (method != other.method) return false
        if (headers != other.headers) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (format != other.format) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + (headers?.hashCode() ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + format.hashCode()
        return result
    }
}