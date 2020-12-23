package com.verygoodsecurity.vgsshow.core.network.model.data.request

import com.verygoodsecurity.vgsshow.core.network.extension.toJsonOrNull
import org.json.JSONObject

class JsonRequestData : RequestData {

    private val data: JSONObject?

    constructor(data: Map<String, Any>) {
        this.data = data.toJsonOrNull()
    }

    constructor(data: String) {
        this.data = data.toJsonOrNull()
    }

    override fun getData(): ByteArray? = data?.toString()?.toByteArray(Charsets.UTF_8)

    override fun isValid(): Boolean = data != null
}