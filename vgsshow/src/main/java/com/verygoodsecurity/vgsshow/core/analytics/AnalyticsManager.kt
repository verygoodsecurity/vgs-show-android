package com.verygoodsecurity.vgsshow.core.analytics

import android.os.Build
import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.Session
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.analytics.event.Event
import com.verygoodsecurity.vgsshow.core.analytics.event.Status
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.extension.toJsonOrNull
import com.verygoodsecurity.vgsshow.core.network.headers.AnalyticsStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.util.connection.NetworkConnectionHelper
import java.util.*

internal class AnalyticsManager constructor(
    tenantId: String,
    environment: VGSEnvironment,
    isSatelliteMode: Boolean,
    private val connectionHelper: NetworkConnectionHelper
) : IAnalyticsManager {

    override var isEnabled: Boolean = true

    private val requestManager: IHttpRequestManager by lazy {
        HttpRequestManager(BASE_URL, getHeadersStore(), false)
    }

    private val defaultInfo: Map<String, Any> = mapOf(
        KEY_SATELLITE to isSatelliteMode,
        KEY_SESSION_ID to Session.id,
        KEY_FORM_ID to UUID.randomUUID().toString(),
        KEY_SOURCE to ANDROID_SDK,
        KEY_TENANT_ID to tenantId,
        KEY_ENVIRONMENT to environment.value,
        KEY_VERSION to BuildConfig.VERSION_NAME,
        KEY_STATUS to Status.OK.value,
        KEY_USER_AGENT to mapOf(
            KEY_PLATFORM to ANDROID,
            KEY_DEVICE to Build.BRAND,
            KEY_DEVICE_MODEL to Build.MODEL,
            KEY_DEVICE_OS to Build.VERSION.SDK_INT.toString()
        )
    )

    override fun log(event: Event) {
        if (isEnabled && connectionHelper.isNetworkPermissionsGranted() && connectionHelper.isNetworkConnectionAvailable()) {
            val payload = (defaultInfo + event.attributes).toJsonOrNull().toString()
            requestManager.enqueue(buildRequest(payload), null)
        }
    }

    override fun cancelAll() {
        requestManager.cancelAll()
    }

    private fun getHeadersStore() = AnalyticsStaticHeadersStore()

    private fun buildRequest(payload: String): VGSRequest =
        VGSRequest.Builder(PATH, VGSHttpMethod.POST)
            .body(payload, VGSHttpBodyFormat.X_WWW_FORM_URLENCODED)
            .build()

    companion object {

        private const val BASE_URL = "https://vgs-collect-keeper.apps.verygood.systems"
        private const val PATH = "/vgs"

        private const val KEY_SATELLITE = "vgsSatellite"
        private const val KEY_SESSION_ID = "vgsShowSessionId"
        private const val KEY_FORM_ID = "formId"
        private const val KEY_SOURCE = "source"
        private const val KEY_TENANT_ID = "tnt"
        private const val KEY_ENVIRONMENT = "env"
        private const val KEY_VERSION = "version"
        private const val KEY_STATUS = "status"

        private const val KEY_USER_AGENT = "ua"
        private const val KEY_PLATFORM = "platform"
        private const val KEY_DEVICE = "device"
        private const val KEY_DEVICE_MODEL = "deviceModel"
        private const val KEY_DEVICE_OS = "osVersion"

        private const val ANDROID = "android"
        private const val ANDROID_SDK = "show-androidSDK"
    }
}