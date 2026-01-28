package com.verygoodsecurity.vgsshow.util.extension

/**
 * Merges two maps.
 * @suppress Not for public use.
 *
 * @param map The map to merge with.
 * @return The merged map.
 */
internal operator fun <K, V> Map<out K, V>?.plus(map: Map<out K, V>?): Map<K, V> =
    LinkedHashMap<K, V>().apply {
        this@plus?.let { this.putAll(it) }
        map?.let { this.putAll(it) }
    }

/**
 * Adds a pair to a map.
 * @suppress Not for public use.
 *
 * @param pair The pair to add.
 * @return The new map.
 */
internal infix fun <K, V> Map<out K, V>?.plusItem(pair: Pair<K, V>): Map<K, V> =
    LinkedHashMap<K, V>().apply {
        this@plusItem?.let { this.putAll(it) }
        put(pair.first, pair.second)
    }
