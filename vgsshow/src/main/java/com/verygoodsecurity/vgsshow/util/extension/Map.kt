package com.verygoodsecurity.vgsshow.util.extension

operator fun <K, V> Map<out K, V>.plus(map: Map<out K, V>?): Map<K, V> =
    LinkedHashMap(this).apply { map?.let { this.putAll(map) } }