package com.verygoodsecurity.vgsshow.core.network.headers

/**
 * An abstract base class for implementing [StaticHeadersStore].
 * @suppress Not for public use.
 */
internal abstract class BaseHeadersStore : StaticHeadersStore {

    /**
     * The default static headers to be included in all requests.
     */
    abstract val defaultStaticHeaders: Map<String, String>

    private val staticHeaders: MutableMap<String, String> = HashMap()

    override fun add(key: String, value: String) {
        staticHeaders[key] = value
    }

    override fun remove(key: String) {
        staticHeaders.remove(key)
    }

    override fun clear() {
        staticHeaders.clear()
    }

    override fun getAll(): Map<String, String> = staticHeaders + defaultStaticHeaders

    override fun getCustom(): Map<String, String> = staticHeaders
}