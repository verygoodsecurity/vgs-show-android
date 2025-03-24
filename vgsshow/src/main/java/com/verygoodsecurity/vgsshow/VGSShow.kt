package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import androidx.annotation.*
import androidx.annotation.IntRange
import com.verygoodsecurity.sdk.analytics.VGSSharedAnalyticsManager
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsStatus
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toVGSEnvironment
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.helper.ViewsStore
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.headers.ProxyStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.BaseNetworkConnectionHelper
import com.verygoodsecurity.vgsshow.util.connection.NetworkConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.*
import com.verygoodsecurity.vgsshow.util.url.UrlHelper.buildLocalhostUrl
import com.verygoodsecurity.vgsshow.util.url.UrlHelper.buildProxyUrl
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import java.util.UUID

private const val SOURCE_TAG = "show-androidSDK"
private const val DEPENDENCY_MANAGER = "maven"

/**
 * VGS Show - Android SDK that enables you to securely display sensitive data.
 * @see <a href="https://www.verygoodsecurity.com/docs/vgs-show">www.verygoodsecurity.com</a>
 *
 * Allows reveal secure data into secure views. Entry-point into Show SDK.
 */
class VGSShow {

    private val context: Context
    private val vaultId: String
    private val environment: VGSEnvironment
    private val formId: String = UUID.randomUUID().toString()
    private var url: String?
    private var port: Int?
    private val listeners: MutableSet<VGSOnResponseListener> by lazy { mutableSetOf() }
    private val viewsStore = ViewsStore()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val headersStore: ProxyStaticHeadersStore
    private val connectionHelper: NetworkConnectionHelper
    private val proxyRequestManager: IHttpRequestManager
    private var isSatelliteMode: Boolean = false
    private var hasCustomHostname: Boolean = false
    private val analyticsManager: VGSSharedAnalyticsManager
    private val onTextCopyListener: VGSTextView.OnTextCopyListener
    private val onSecureTextRangeListener: VGSTextView.OnSetSecureTextRangeSetListener
    private val onRenderStateChangeListener: VGSPDFView.OnRenderStateChangeListener
    private val onShareDocumentListener: VGSPDFView.OnShareDocumentListener

    /**
     * Constructor that allows specify environment as object.
     *
     * @param context lifecycle owner context.
     * @param vaultId unique vault id.
     * @param environment type of vault. @see [com.verygoodsecurity.vgsshow.core.VGSEnvironment]
     */
    constructor(
        context: Context,
        vaultId: String,
        environment: VGSEnvironment
    ) : this(context, vaultId, environment, null, null, null, null)

    /**
     * Constructor that allows specify environment as string.
     *
     * @param context lifecycle owner context.
     * @param vaultId unique vault id.
     * @param environment type of vault, ex. "sandbox-eu-1"
     */
    constructor(
        context: Context,
        vaultId: String,
        environment: String
    ) : this(context, vaultId, environment.toVGSEnvironment(), null, null, null, null)

    internal constructor(
        context: Context,
        vaultId: String,
        environment: VGSEnvironment,
        url: String?,
        port: Int?,
        headerStore: ProxyStaticHeadersStore?,
        analyticsManager: VGSSharedAnalyticsManager?
    ) {
        this.context = context
        this.vaultId = vaultId
        this.environment = environment
        this.url = url
        this.port = port
        this.connectionHelper = BaseNetworkConnectionHelper(context)
        this.headersStore = headerStore ?: ProxyStaticHeadersStore()
        this.analyticsManager = analyticsManager ?: VGSSharedAnalyticsManager(
            source = SOURCE_TAG,
            sourceVersion = BuildConfig.VERSION_NAME,
            dependencyManager = DEPENDENCY_MANAGER
        )
        this.proxyRequestManager = buildNetworkManager()
        this.onTextCopyListener = object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                capture(
                    VGSAnalyticsEvent.CopyToClipboard(
                        view.getFieldType().toAnalyticTag(),
                        view.getContentPath(),
                        format.toAnalyticsFormat()
                    )
                )
            }
        }
        this.onSecureTextRangeListener = object : VGSTextView.OnSetSecureTextRangeSetListener {

            override fun onSecureTextRangeSet(view: VGSTextView) {
                capture(
                    VGSAnalyticsEvent.SecureTextRange(
                        view.getFieldType().toAnalyticTag(),
                        view.getContentPath()
                    )
                )
            }
        }
        this.onRenderStateChangeListener = object : VGSPDFView.OnRenderStateChangeListener {

            override fun onStart(view: VGSPDFView, pages: Int) {}

            override fun onComplete(view: VGSPDFView, pages: Int) {
                capture(
                    VGSAnalyticsEvent.ContentRendering(
                        VGSAnalyticsStatus.OK,
                        view.getFieldType().toAnalyticTag(),
                        view.getContentPath()
                    )
                )
            }

            override fun onError(view: VGSPDFView, t: Throwable) {
                capture(
                    VGSAnalyticsEvent.ContentRendering(
                        VGSAnalyticsStatus.FAILED,
                        view.getFieldType().toAnalyticTag(),
                        view.getContentPath()
                    )
                )
            }
        }
        this.onShareDocumentListener = object : VGSPDFView.OnShareDocumentListener {

            override fun onShare(view: VGSPDFView) {
                capture(VGSAnalyticsEvent.ContentSharing(view.getContentPath()))
            }
        }
    }

    /**
     * Synchronous request for reveal data. Note: This function should be executed in background thread.
     * @throws android.os.NetworkOnMainThreadException if this function executes in main thread.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     * @param payload key-value data
     */
    @JvmOverloads
    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun request(
        path: String,
        method: VGSHttpMethod,
        payload: Map<String, Any>? = null
    ): VGSResponse =
        request(VGSRequest.Builder(path, method).body(payload).build())

    /**
     * Synchronous request for reveal data. Note: This function should be executed in background thread.
     * @throws android.os.NetworkOnMainThreadException if this function executes in main thread.
     *
     * @param request @see [com.verygoodsecurity.vgsshow.core.network.model.VGSRequest]
     */
    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun request(request: VGSRequest): VGSResponse {
        return when {
            !connectionHelper.isNetworkPermissionsGranted() -> {
                VGSException.NoInternetPermission().toVGSResponse()
            }

            !connectionHelper.isNetworkConnectionAvailable() -> {
                VGSException.NoInternetConnection().toVGSResponse()
            }

            else -> {
                logRequestEvent(request)
                with(proxyRequestManager.execute(request)) {
                    logResponseEvent(this)
                    mainHandler.post { viewsStore.update((this as? VGSResponse.Success)?.data) }
                    this
                }
            }
        }
    }

    /**
     * Asynchronous request for reveal data.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     * @param payload key-value data
     */
    @JvmOverloads
    @AnyThread
    fun requestAsync(path: String, method: VGSHttpMethod, payload: Map<String, Any>? = null) {
        requestAsync(VGSRequest.Builder(path, method).body(payload).build())
    }

    /**
     * Asynchronous request for reveal data
     *
     * @param request @see [com.verygoodsecurity.vgsshow.core.network.model.VGSRequest]
     */
    @AnyThread
    fun requestAsync(request: VGSRequest) {
        if (!connectionHelper.isNetworkPermissionsGranted()) {
            handleResponse(VGSException.NoInternetPermission().toVGSResponse())
            return
        } else if (!connectionHelper.isNetworkConnectionAvailable()) {
            handleResponse(VGSException.NoInternetConnection().toVGSResponse())
            return
        }
        logRequestEvent(request)
        proxyRequestManager.enqueue(request) {
            logResponseEvent(it)
            handleResponse(it)
        }
    }

    /**
     * Apply previously stored [VGSResponse.Success]. This function interact with UI and should bu called from main thread.
     */
    @MainThread
    fun applyResponse(response: VGSResponse.Success) {
        updateViews(response)
    }

    /**
     * Adds a listener to the list of those whose methods are called whenever the VGSShow receive response from Server.
     *
     * @param listener Interface definition for a receiving callback. @see[com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener]
     */
    fun addOnResponseListener(listener: VGSOnResponseListener) {
        listeners.add(listener)
    }

    /**
     * Clear specific listener attached before.
     *
     * @param listener Interface definition for a receiving callback. @see[com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener]
     */
    fun removeOnResponseListener(listener: VGSOnResponseListener) {
        listeners.remove(listener)
    }

    /**
     * Clear all response listeners.
     */
    fun clearResponseListeners() {
        listeners.clear()
    }

    /**
     * Allows [VGSShow] to interact with VGS secure views.
     *
     * @param view VGS secure view. @see [com.verygoodsecurity.vgsshow.widget.VGSTextView], [com.verygoodsecurity.vgsshow.widget.VGSPDFView]
     */
    fun subscribe(view: VGSView<*>) {
        if (viewsStore.add(view)) {
            capture(
                VGSAnalyticsEvent.FieldAttach(
                    view.getFieldType().toAnalyticTag(),
                    view.getContentPath()
                )
            )
            when (view) {
                is VGSTextView -> handleTextViewSubscription(view)
                is VGSPDFView -> handlePDFViewSubscription(view)
            }
        }
    }

    /**
     * Remove previously added VGS secure view
     *
     * @param view VGS secure view. @see [com.verygoodsecurity.vgsshow.widget.VGSTextView]
     */
    fun unsubscribe(view: VGSView<*>) {
        if (viewsStore.remove(view)) {
            capture(VGSAnalyticsEvent.FieldDetach(view.getFieldType().toAnalyticTag()))
            if (view is VGSTextView) {
                view.removeOnCopyTextListener(onTextCopyListener)
                view.setOnSecureTextRangeSetListener(null)
            }
        }
    }

    /**
     * Used to edit static request headers that will be added to all requests of this VGSShow instance.
     */
    fun setCustomHeader(header: String, value: String) {
        headersStore.add(header, value)
    }

    /**
     * Used to edit static request headers that will be added to all requests of this VGSShow instance.
     */
    fun removeCustomHeader(header: String) {
        headersStore.remove(header)
    }

    /**
     * Used to edit static request headers that will be added to all requests of this VGSShow instance.
     */
    fun clearCustomHeaders() {
        headersStore.clear()
    }

    /**
     * Used to enable/disable analytics events.
     *
     * @param isEnabled true if VGSShow should send analytics events.
     */
    fun setAnalyticsEnabled(isEnabled: Boolean) {
        analyticsManager.setIsEnabled(isEnabled)
        headersStore.isAnalyticsEnabled = isEnabled
    }

    /**
     * Used to determine if analytics enabled/disabled.
     *
     * @return true if VGSShow analytics enabled, false otherwise.
     */
    fun getIsAnalyticsEnabled() = analyticsManager.getIsEnabled()

    /**
     * Clear all information collected before by VGSShow, cancel all network requests.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        proxyRequestManager.cancelAll()
        analyticsManager.cancelAll()
        listeners.clear()
        clearViewsListeners()
        viewsStore.clear()
        headersStore.clear()
    }

    @VisibleForTesting
    internal fun getResponseListeners() = listeners

    @VisibleForTesting
    internal fun getViewsStore() = viewsStore

    private fun buildNetworkManager(): IHttpRequestManager {

        fun printPortDenied() {
            if (port.isValidPort()) {
                logWaring("To protect your device we allow to use PORT only on localhost. PORT will be ignored")
            }
        }

        return url?.takeIf { it.isValidUrl() }?.let { url ->
            val host = getHost(url)
            return if (host.isValidIp()) {
                if (!host.isIpAllowed()) {
                    logWaring("Current IP is not allowed, use localhost or private network IP.")
                    return HttpRequestManager(buildProxyUrl(vaultId, environment), headersStore)
                }
                if (environment !is VGSEnvironment.Sandbox) {
                    logWaring("Custom local IP and PORT can be used only in a sandbox environment.")
                    return HttpRequestManager(buildProxyUrl(vaultId, environment), headersStore)
                }
                isSatelliteMode = true
                HttpRequestManager(buildLocalhostUrl(host, port), headersStore)
            } else {
                HttpRequestManager(buildProxyUrl(vaultId, environment), headersStore).also {
                    printPortDenied()
                    setCname(it, host)
                }
            }
        } ?: HttpRequestManager(buildProxyUrl(vaultId, environment), headersStore).also {
            printPortDenied()
        }
    }

    private fun getHost(url: String) = url.toHost().also {
        if (it != url) {
            logDebug("Hostname will be normalized to the $it")
        }
    }

    private fun setCname(manager: IHttpRequestManager, cname: String?) {
        manager.setCname(vaultId, cname) { isSuccessful, latency ->
            hasCustomHostname = isSuccessful
            capture(
                VGSAnalyticsEvent.Cname(
                    status = if (isSuccessful) VGSAnalyticsStatus.OK else VGSAnalyticsStatus.FAILED,
                    hostname = cname ?: "",
                    latency = latency
                )
            )
        }
    }

    private fun handleTextViewSubscription(view: VGSTextView) {
        view.addOnCopyTextListener(onTextCopyListener)
        view.setOnSecureTextRangeSetListener(onSecureTextRangeListener)
        view.onViewSubscribed()
    }

    private fun handlePDFViewSubscription(view: VGSPDFView) {
        view.addRenderingStateChangedListener(onRenderStateChangeListener)
        view.onShareDocumentListener = onShareDocumentListener
    }

    @MainThread
    private fun notifyResponseListeners(response: VGSResponse) {
        listeners.forEach {
            it.onResponse(response)
        }
    }

    private fun handleResponse(response: VGSResponse) {
        mainHandler.post {
            updateViews(response)
            notifyResponseListeners(response)
        }
    }

    private fun updateViews(response: VGSResponse) {
        viewsStore.update((response as? VGSResponse.Success)?.data)
    }

    private fun logRequestEvent(request: VGSRequest) {
        val eventBuilder = VGSAnalyticsEvent.Request.Builder(VGSAnalyticsStatus.OK, code = 200)
        if (viewsStore.getViews().any { it is VGSTextView }) {
            eventBuilder.fields()
        }
        if (viewsStore.getViews().any { it is VGSPDFView }) {
            eventBuilder.pdf()
        }
        if (request.payload != null) {
            eventBuilder.customData()
        }
        if (!request.headers.isNullOrEmpty() || headersStore.getCustom().isNotEmpty()) {
            eventBuilder.customHeader()
        }
        if (hasCustomHostname) {
            eventBuilder.customHostname()
        }
        capture(eventBuilder.build())
    }

    private fun logResponseEvent(response: VGSResponse) {
        capture(
            VGSAnalyticsEvent.Response(
                status = if (response is VGSResponse.Success) VGSAnalyticsStatus.OK else VGSAnalyticsStatus.FAILED,
                code = response.code,
                errorMessage = (response as? VGSResponse.Error)?.message
            )
        )
    }

    private fun clearViewsListeners() {
        viewsStore.getViews().forEach {
            when (it) {
                is VGSTextView -> it.removeOnCopyTextListener(onTextCopyListener)
                is VGSPDFView -> it.removeRenderingStateChangedListener(onRenderStateChangeListener)
            }
        }
    }

    private fun capture(event: VGSAnalyticsEvent) {
        analyticsManager.capture(
            vault = vaultId,
            environment = environment.value,
            formId = formId,
            event = event
        )
    }

    /**
     * Used to create VGSShow instances with default and overridden settings.
     *
     * @constructor create VGSShow instance builder.
     * @param context lifecycle owner context.
     * @param id unique vault id.
     */
    class Builder constructor(private val context: Context, private val id: String) {

        private var environment: VGSEnvironment = VGSEnvironment.Sandbox()
        private var host: String? = null
        private var port: Int? = null

        /** Specify Environment for the VGSShow instance. */
        fun setEnvironment(environment: VGSEnvironment) =
            this.apply { this.environment = environment }

        /**
         * Sets the VGSShow instance to use the custom hostname.
         * Also, the localhost IP can be used for VGS-Satellite for local testing.
         *
         * @param cname where VGSShow will send requests.
         */
        fun setHostname(cname: String) = this.apply {
            if (!cname.isValidUrl()) {
                logWaring("A specified host($host) is not valid url.")
                return@apply
            }
            this.host = cname
        }

        /**
         * Sets the VGSShow instance to use the custom hostname port.
         * Port can be used only with localhost with VGS-Satellite, otherwise, it will be ignored.
         *
         * @param port Integer value from 1 to 65353.
         */
        fun setPort(@IntRange(from = PORT_MIN_VALUE, to = PORT_MAX_VALUE) port: Int) =
            this.apply { this.port = port }

        /** Build VGSShow instance */
        fun build() = VGSShow(context, id, environment, host, port, null, null)
    }
}