package com.verygoodsecurity.vgsshow.core.network.cache

internal class CustomHeaderStore : IVGSCustomHeaderStore {

    private val cachedHeaders: MutableMap<String, String> = HashMap()

    override fun addHeader(key: String, value: String) {
        cachedHeaders[key] = value
    }

    override fun removeHeader(key: String) {
        cachedHeaders.remove(key)
    }

    override fun getHeaders(): Map<String, String> = cachedHeaders

    override fun clearHeaders() {
        cachedHeaders.clear()
    }
}