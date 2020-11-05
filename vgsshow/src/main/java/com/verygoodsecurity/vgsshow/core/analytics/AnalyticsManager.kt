package com.verygoodsecurity.vgsshow.core.analytics

import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper

internal class AnalyticsManager constructor(
    environment: VGSEnvironment,
    connectionHelper: IConnectionHelper
) : IAnalyticsManager {

    private val requestManager: IHttpRequestManager by lazy {
        HttpRequestManager(getBaseUrl(environment), null, connectionHelper)
    }

    override fun log(event: Event) {
        requestManager.enqueue(VGSRequest.Builder(PATH, VGSHttpMethod.POST).build(), null)
    }

    override fun cancelAll() {
        requestManager.cancelAll()
    }

    private fun getBaseUrl(environment: VGSEnvironment) = when (environment) {
        is VGSEnvironment.Live -> LIVE_BASE_URL
        else -> SANDBOX_BASE_URL
    }

    companion object {

        private const val LIVE_BASE_URL = "https://vgs-collect-keeper.apps.verygood.systems"
        private const val SANDBOX_BASE_URL = "https://vgs-collect-keeper.verygoodsecurity.io"

        private const val PATH = "/vgs"
    }
}