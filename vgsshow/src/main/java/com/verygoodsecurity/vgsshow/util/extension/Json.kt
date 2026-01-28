package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONArray
import org.json.JSONObject

/**
 * Gets a value from a JSONObject using a dot-separated path.
 * @suppress Not for public use.
 *
 * @param path The dot-separated path to the value.
 * @return The value as a string, or `null` if the value is not found.
 */
internal fun JSONObject.getValue(path: String): String? {
    var value: Any? = this

    path.split(".").forEach { key ->
        value = (value as? JSONObject)?.run {
            if (has(key)) {
                this
            } else {
                null
            }
        }?.get(key)?.run {
            when (this) {
                is JSONObject -> this
                is JSONArray -> this
                else -> this
            }
        }
    }

    return value?.toString()
}
