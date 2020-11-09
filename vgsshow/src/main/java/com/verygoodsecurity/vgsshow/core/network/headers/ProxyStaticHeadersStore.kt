package com.verygoodsecurity.vgsshow.core.network.headers

internal class ProxyStaticHeadersStore : IVGSStaticHeadersStore {

    private val staticHeaders: MutableMap<String, String> = HashMap()

    private val defaultStaticHeaders: Map<String, String> = mapOf()

    override fun add(key: String, value: String) {
        staticHeaders[key] = value
    }

    override fun remove(key: String) {
        staticHeaders.remove(key)
    }

    override fun getAll(): Map<String, String> = staticHeaders + defaultStaticHeaders

    override fun containsUserHeaders(): Boolean = staticHeaders.isNotEmpty()

    override fun clear() {
        staticHeaders.clear()
    }
}