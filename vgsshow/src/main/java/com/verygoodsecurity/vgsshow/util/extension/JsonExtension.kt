package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONArray
import org.json.JSONObject


internal fun JSONObject.getValue(path: String): String {
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
                is JSONArray -> {
                    this
                }
                is Any -> {
                    this
                }
                else -> {
                    null
                }
            }
        }
    }

    return value.toString()
}
