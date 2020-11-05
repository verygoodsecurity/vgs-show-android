package com.verygoodsecurity.vgsshow.core.network.cache

internal class CustomHeaderStore : IVGSCustomHeaderStore {

    private val cachedHeaders: MutableMap<String, String> = HashMap()

    override fun add(key: String, value: String) {
        cachedHeaders[key] = value
    }

    override fun remove(key: String) {
        cachedHeaders.remove(key)
    }

    override fun getAll(): Map<String, String> = cachedHeaders

    override fun clear() {
        cachedHeaders.clear()
    }
}