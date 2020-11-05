package com.verygoodsecurity.vgsshow.util.extension

operator fun <K, V> Map<out K, V>?.plus(map: Map<out K, V>?): Map<K, V> =
    LinkedHashMap<K, V>().apply {
        this@plus?.let { this.putAll(it) }
        map?.let { this.putAll(it) }
    }