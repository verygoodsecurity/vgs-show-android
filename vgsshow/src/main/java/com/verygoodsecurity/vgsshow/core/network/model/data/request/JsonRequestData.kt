package com.verygoodsecurity.vgsshow.core.network.model.data.request

import com.verygoodsecurity.vgsshow.core.network.extension.toJsonOrNull
import org.json.JSONObject

/**
 * Represents JSON request data.
 * @suppress Not for public use.
 */
internal class JsonRequestData : RequestData {

    private val data: JSONObject?

    /**
     * @param data The map data to be converted to JSON.
     */
    constructor(data: Map<String, Any>) {
        this.data = data.toJsonOrNull()
    }

    /**
     * @param data The string data to be converted to JSON.
     */
    constructor(data: String) {
        this.data = data.toJsonOrNull()
    }

    override fun getRawData(): String? = data?.toString()

    override fun getData(): ByteArray? = data?.toString()?.toByteArray(Charsets.UTF_8)

    override fun isValid(): Boolean = data != null
}