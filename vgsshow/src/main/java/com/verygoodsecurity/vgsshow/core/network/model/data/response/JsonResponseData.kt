@file:Suppress("CanBeParameter")

package com.verygoodsecurity.vgsshow.core.network.model.data.response

import com.verygoodsecurity.vgsshow.util.extension.getValue
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONException
import org.json.JSONObject

/**
 * Represents JSON response data.
 * @suppress Not for public use.
 *
 * @param data The JSON string data.
 * @throws JSONException if the data is not a valid JSON string.
 */
@Parcelize
internal class JsonResponseData @Throws(JSONException::class) constructor(
    private val data: String
) : ResponseData {

    @IgnoredOnParcel
    private val json = JSONObject(data)

    override fun getValue(key: String) = json.getValue(key)
}