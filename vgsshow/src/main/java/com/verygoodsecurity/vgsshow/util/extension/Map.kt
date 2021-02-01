package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONObject

internal operator fun <K, V> Map<out K, V>?.plus(map: Map<out K, V>?): Map<K, V> =
    LinkedHashMap<K, V>().apply {
        this@plus?.let { this.putAll(it) }
        map?.let { this.putAll(it) }
    }

internal infix fun <K, V> Map<out K, V>?.plusItem(pair: Pair<K, V>): Map<K, V> =
    LinkedHashMap<K, V>().apply {
        this@plusItem?.let { this.putAll(it) }
        put(pair.first, pair.second)
    }

internal fun Map<String, Any>.isValidJson() = try {
    JSONObject(this)
    true
} catch (e: Exception) {
    false
}
