package com.verygoodsecurity.vgsshow.core.network.headers

import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session
import com.verygoodsecurity.vgsshow.core.logs.VGSShowLogger

internal abstract class BaseHeadersStore : StaticHeadersStore {

    protected open val defaultStaticHeaders: Map<String, String>
        get() = mapOf(
            AGENT_HEADER to "source=show-androidSD" +
                    "K&medium=vgs-show" +
                    "&content=${BuildConfig.VERSION_NAME}" +
                    "&vgsShowSessionId=${Session.id}" +
                    "&logLevel=${if (VGSShowLogger.level == VGSShowLogger.Level.NONE) "none" else "default"}"
        )

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

    override fun containsUserHeaders(): Boolean = staticHeaders.isNotEmpty()

    companion object {

        private const val AGENT_HEADER = "vgs-client"
    }
}