package com.verygoodsecurity.vgsshow.core.network.headers

import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session

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