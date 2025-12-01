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
 * Orchestrates the secure display of sensitive data from a VGS Vault.
 *
 * `VGSShow` is the primary entry point for the VGS Show SDK. It is responsible for:
 * - Configuring the connection to your VGS Vault (using your vault ID and environment).
 * - Making network requests to reveal sensitive data.
 * - Subscribing secure UI views (`VGSTextView`, `VGSPDFView`) to display the revealed data.
 *
 * @see [VGSShow Documentation](https://www.verygoodsecurity.com/docs/vgs-show)
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
     * Creates a new `VGSShow` instance.
     *
     * This constructor is the standard way to initialize `VGSShow`.
     * It requires the Android `Context`, your unique `vaultId`, and the `VGSEnvironment`.
     *
     * @param context The Activity or Application context.
     * @param vaultId Your unique vault identifier.
     * @param environment The VGS environment (e.g., `VGSEnvironment.SANDBOX` or `VGSEnvironment.LIVE`).
     */
    constructor(
        context: Context,
        vaultId: String,
        environment: VGSEnvironment
    ) : this(context, vaultId, environment, null, null, null, null)

    /**
     * Creates a new `VGSShow` instance using a string for the environment.
     *
     * @param context The Activity or Application context.
     * @param vaultId Your unique vault identifier.
     * @param environment The environment as a string (e.g., "sandbox-eu-1").
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
     * Executes a synchronous request to reveal data.
     *
     * This method blocks the current thread until a response is received. It **must not** be called
     * on the main thread; doing so will result in a [NetworkOnMainThreadException]. For main-thread-safe
     * operations, use [requestAsync].
     *
     * @param path The endpoint path for the request (e.g., "/post").
     * @param method The HTTP method to use. See [VGSHttpMethod].
     * @param payload Optional key-value data to be sent in the request body.
     * @return A [VGSResponse] object containing either the successful response data or an error.
     * @throws NetworkOnMainThreadException if called on the main UI thread.
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
     * Executes a synchronous request to reveal data using a pre-built [VGSRequest].
     *
     * This method blocks the current thread until a response is received. It **must not** be called
     * on the main thread. For main-thread-safe operations, use [requestAsync].
     *
     * @param request A [VGSRequest] object defining the request parameters.
     * @return A [VGSResponse] object containing either the successful response data or an error.
     * @throws NetworkOnMainThreadException if called on the main UI thread.
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
     * Executes an asynchronous request to reveal data.
     *
     * This method is safe to call from any thread. The result will be delivered to any registered
     * [VGSOnResponseListener] on the main thread.
     *
     * @param path The endpoint path for the request (e.g., "/post").
     * @param method The HTTP method to use. See [VGSHttpMethod].
     * @param payload Optional key-value data to be sent in the request body.
     */
    @JvmOverloads
    @AnyThread
    fun requestAsync(path: String, method: VGSHttpMethod, payload: Map<String, Any>? = null) {
        requestAsync(VGSRequest.Builder(path, method).body(payload).build())
    }

    /**
     * Executes an asynchronous request to reveal data using a pre-built [VGSRequest].
     *
     * This method is safe to call from any thread. The result will be delivered to any registered
     * [VGSOnResponseListener] on the main thread.
     *
     * @param request A [VGSRequest] object defining the request parameters.
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
     * Manually applies a successful response to all subscribed views.
     *
     * This is useful if you have a previously stored [VGSResponse.Success] and want to re-populate
     * views without making a new network request. This method must be called from the main thread.
     *
     * @param response The successful response to apply.
     */
    @MainThread
    fun applyResponse(response: VGSResponse.Success) {
        updateViews(response)
    }

    /**
     * Registers a listener to receive server responses.
     *
     * The listener will be invoked on the main thread when a response is received from the VGS proxy.
     *
     * @param listener The listener to add. See [VGSOnResponseListener].
     */
    fun addOnResponseListener(listener: VGSOnResponseListener) {
        listeners.add(listener)
    }

    /**
     * Removes a previously registered response listener.
     *
     * @param listener The listener to remove. See [VGSOnResponseListener].
     */
    fun removeOnResponseListener(listener: VGSOnResponseListener) {
        listeners.remove(listener)
    }

    /**
     * Removes all registered response listeners.
     */
    fun clearResponseListeners() {
        listeners.clear()
    }

    /**
     * Subscribes a [VGSView] to this `VGSShow` instance.
     *
     * Once subscribed, the view will be populated with data from network responses.
     *
     * @param view The secure view to subscribe (e.g., [VGSTextView], [VGSPDFView]).
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
     * Unsubscribes a [VGSView] from this `VGSShow` instance.
     *
     * The view will no longer receive data from network responses.
     *
     * @param view The secure view to unsubscribe.
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
     * Sets a custom HTTP header to be sent with every request.
     *
     * @param header The name of the header (e.g., "Authorization").
     * @param value The value of the header.
     */
    fun setCustomHeader(header: String, value: String) {
        headersStore.add(header, value)
    }

    /**
     * Removes a custom HTTP header.
     *
     * @param header The name of the header to remove.
     */
    fun removeCustomHeader(header: String) {
        headersStore.remove(header)
    }

    /**
     * Removes all custom HTTP headers.
     */
    fun clearCustomHeaders() {
        headersStore.clear()
    }

    /**
     * Toggles VGS Analytics event collection.
     *
     * @param isEnabled `true` to enable analytics, `false` to disable.
     */
    fun setAnalyticsEnabled(isEnabled: Boolean) {
        analyticsManager.setIsEnabled(isEnabled)
        headersStore.isAnalyticsEnabled = isEnabled
    }

    /**
     * Checks if VGS Analytics event collection is enabled.
     *
     * @return `true` if enabled, `false` otherwise.
     */
    fun getIsAnalyticsEnabled() = analyticsManager.getIsEnabled()

    /**
     * Cleans up resources used by this `VGSShow` instance.
     * Call this in your `Activity` or `Fragment`'s `onDestroy` method to prevent memory leaks.
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