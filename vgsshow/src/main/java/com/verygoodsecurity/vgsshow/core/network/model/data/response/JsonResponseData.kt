@file:Suppress("CanBeParameter")

package com.verygoodsecurity.vgsshow.core.network.model.data.response

import com.verygoodsecurity.vgsshow.util.extension.getValue
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONException
import org.json.JSONObject

@Parcelize
internal class JsonResponseData @Throws(JSONException::class) constructor(
    private val data: String
) : ResponseData {

    @IgnoredOnParcel
    private val json = JSONObject(data)

    override fun getValue(key: String) = json.getValue(key)
}