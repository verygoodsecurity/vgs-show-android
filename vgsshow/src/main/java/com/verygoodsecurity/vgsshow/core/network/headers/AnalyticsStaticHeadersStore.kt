package com.verygoodsecurity.vgsshow.core.network.headers

/**
 * Restrict ability to mistakenly add extra headers
 */
internal class AnalyticsStaticHeadersStore : BaseHeadersStore() {

    override val defaultStaticHeaders: Map<String, String>
        get() = emptyMap()

    override fun add(key: String, value: String) {
        // unused
    }

    override fun remove(key: String) {
        // unused
    }

    override fun clear() {
        // unused
    }
}