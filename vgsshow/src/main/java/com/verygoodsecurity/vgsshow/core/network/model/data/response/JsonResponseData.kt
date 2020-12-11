package com.verygoodsecurity.vgsshow.core.network.model.data.response

import com.verygoodsecurity.vgsshow.util.extension.getValue
import org.json.JSONObject

internal class JsonResponseData constructor(
    private val data: JSONObject
) : ResponseData {

    override fun getValue(key: String) = data.getValue(key)
}