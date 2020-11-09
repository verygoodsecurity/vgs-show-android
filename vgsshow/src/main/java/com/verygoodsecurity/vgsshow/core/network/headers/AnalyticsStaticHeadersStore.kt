package com.verygoodsecurity.vgsshow.core.network.headers

/**
 * Analytics headers store, as currently analytics doesn't require headers it's empty
 */
class AnalyticsStaticHeadersStore : IVGSStaticHeadersStore {

    override fun add(key: String, value: String) {
        // unused
    }

    override fun remove(key: String) {
        // unused
    }

    override fun getAll(): Map<String, String> = emptyMap()

    override fun containsUserHeaders(): Boolean  = false

    override fun clear() {
        // unused
    }
}