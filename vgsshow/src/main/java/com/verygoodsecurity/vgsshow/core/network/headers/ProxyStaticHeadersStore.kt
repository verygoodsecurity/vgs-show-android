package com.verygoodsecurity.vgsshow.core.network.headers

import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session

/**
 * A [StaticHeadersStore] that adds a `vgs-client` header to all requests.
 * @suppress Not for public use.
 *
 * @property isAnalyticsEnabled Whether or not to include analytics data in the header.
 */
internal class ProxyStaticHeadersStore(var isAnalyticsEnabled: Boolean = true) : BaseHeadersStore() {

    override val defaultStaticHeaders: Map<String, String>
        get() = mapOf(
            AGENT_HEADER to "source=show-androidSDK" +
                    "&medium=vgs-show" +
                    "&content=${BuildConfig.VERSION_NAME}" +
                    "&vgsShowSessionId=${Session.id}" +
                    "&tr=${if (isAnalyticsEnabled) "default" else "none"}"
        )

    companion object {

        private const val AGENT_HEADER = "vgs-client"
    }
}