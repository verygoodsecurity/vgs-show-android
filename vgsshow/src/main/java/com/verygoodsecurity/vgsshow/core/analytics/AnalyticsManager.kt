package com.verygoodsecurity.vgsshow.core.analytics

import android.os.Build
import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.analytics.event.Event
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.cache.StaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.util.connection.IConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.toJSON
import java.util.*

internal class AnalyticsManager constructor(
    tenantId: String,
    environment: VGSEnvironment,
    connectionHelper: IConnectionHelper
) : IAnalyticsManager {

    private val requestManager: IHttpRequestManager by lazy {
        HttpRequestManager(getBaseUrl(environment), getHeadersStore(), connectionHelper)
    }

    private val defaultInfo: Map<String, Any> = mapOf(
        KEY_SESSION_ID to Session.id,
        KEY_FORM_ID to UUID.randomUUID().toString(),
        KEY_SOURCE to ANDROID_SDK,
        KEY_TENANT_ID to tenantId,
        KEY_ENVIRONMENT to environment.value,
        KEY_VERSION to BuildConfig.VERSION_NAME,
        KEY_STATUS to OK,
        KEY_USER_AGENT to mapOf(
            KEY_PLATFORM to ANDROID,
            KEY_DEVICE to Build.BRAND,
            KEY_DEVICE_MODEL to Build.MODEL,
            KEY_DEVICE_OS to Build.VERSION.SDK_INT.toString()
        )
    )

    init {

        logDebug("session id = ${Session.id}")
    }

    override fun log(event: Event) {
        logDebug((defaultInfo + event.attributes).toString())
        requestManager.enqueue(buildRequest(event)) {
            logDebug(it.toString())
        }
    }

    override fun cancelAll() {
        requestManager.cancelAll()
    }

    private fun getBaseUrl(environment: VGSEnvironment) = when (environment) {
        is VGSEnvironment.Live -> LIVE_BASE_URL
        else -> SANDBOX_BASE_URL
    }

    private fun getHeadersStore() = StaticHeadersStore()

    private fun buildRequest(event: Event): VGSRequest =
        VGSRequest.Builder(PATH, VGSHttpMethod.POST)
            .body((defaultInfo + event.attributes).toJSON(), VGSHttpBodyFormat.JSON)
            .build()

    companion object {

        private const val PATH = "/vgs"

        private const val LIVE_BASE_URL = "https://vgs-collect-keeper.apps.verygood.systems"
        private const val SANDBOX_BASE_URL = "https://vgs-collect-keeper.verygoodsecurity.io"

        private const val KEY_SESSION_ID = "vgsCollectSessionId"
        private const val KEY_FORM_ID = "formId"
        private const val KEY_SOURCE = "source"
        private const val KEY_TENANT_ID = "tnt"
        private const val KEY_ENVIRONMENT = "env"
        private const val KEY_VERSION = "version"
        private const val KEY_STATUS = "status"

        private const val KEY_USER_AGENT = "ua"
        private const val KEY_PLATFORM = "platform"
        private const val KEY_DEVICE = "device"
        private const val KEY_DEVICE_MODEL = "device"
        private const val KEY_DEVICE_OS = "osVersion"

        private const val ANDROID = "android"
        private const val ANDROID_SDK = "androidSDK"
        private const val OK = "Ok"
    }
}