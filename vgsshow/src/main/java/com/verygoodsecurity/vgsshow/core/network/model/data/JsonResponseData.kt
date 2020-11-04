package com.verygoodsecurity.vgsshow.core.network.model.data

import com.verygoodsecurity.vgsshow.util.extension.getValue
import org.json.JSONObject

class JsonResponseData constructor(
    private val data: JSONObject
) : IResponseData {

    override fun getValue(key: String) = data.getValue(key)
}