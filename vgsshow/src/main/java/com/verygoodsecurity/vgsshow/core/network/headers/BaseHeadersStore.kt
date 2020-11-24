package com.verygoodsecurity.vgsshow.core.network.headers

import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session

internal abstract class BaseHeadersStore : IVGSStaticHeadersStore {

    private val staticHeaders: MutableMap<String, String> = HashMap()

    private val defaultStaticHeaders: Map<String, String> = mapOf(
        AGENT_HEADER to "source=show-androidSdk&medium=vgs-show&content=${BuildConfig.VERSION_NAME}&vgsShowSessionId=${Session.id}"
    )

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

    override fun containsUserHeaders(): Boolean = staticHeaders.isNotEmpty()

    companion object {

        private const val AGENT_HEADER = "vgs-client"
    }
}