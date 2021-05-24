package com.verygoodsecurity.vgsshow

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import androidx.annotation.*
import androidx.annotation.IntRange
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toVGSEnvironment
import com.verygoodsecurity.vgsshow.core.analytics.AnalyticsManager
import com.verygoodsecurity.vgsshow.core.analytics.IAnalyticsManager
import com.verygoodsecurity.vgsshow.core.analytics.event.*
import com.verygoodsecurity.vgsshow.core.analytics.extension.toAnalyticTag
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

/**
 * VGS Show - Android SDK that enables you to securely display sensitive data.
 * @see <a href="https://www.verygoodsecurity.com/docs/vgs-show">www.verygoodsecurity.com</a>
 *
 * Allows reveal secure data into secure views. Entry-point into Show SDK.
 *
 * @constructor create configured, ready to use entry-point into Show SDK.
 * @param context lifecycle owner context.
 * @param vaultId unique vault id.
 * @param environment type of vault. @see [com.verygoodsecurity.vgsshow.core.VGSEnvironment]
 * @param url that will be used as base url. Use for testing.
 * @param port localhost port.
 */
class VGSShow private constructor(
    private val context: Context,
    private val vaultId: String,
    private val environment: VGSEnvironment,
    private var url: String?,
    private var port: Int?
) {

    private val listeners: MutableSet<VGSOnResponseListener> by lazy { mutableSetOf() }

    private val viewsStore = ViewsStore()

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val headersStore: ProxyStaticHeadersStore = ProxyStaticHeadersStore()

    private val connectionHelper: NetworkConnectionHelper = BaseNetworkConnectionHelper(context)

    private val proxyRequestManager: IHttpRequestManager = buildNetworkManager()

    private var isSatelliteMode: Boolean = false

    private var hasCustomHostname: Boolean = false

    private val analyticsManager: IAnalyticsManager =
        AnalyticsManager(vaultId, environment, isSatelliteMode, connectionHelper)

    private val onTextCopyListener = object : VGSTextView.OnTextCopyListener {

        override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
            analyticsManager.log(CopyToClipboardEvent(format))
        }
    }

    private val onSecureTextRangeListener = object : VGSTextView.OnSetSecureTextRangeSetListener {

        override fun onSecureTextRangeSet(view: VGSTextView) {
            analyticsManager.log(
                SetSecureTextEvent(
                    view.getContentPath(),
                    view.getFieldType().toAnalyticTag()
                )
            )
        }
    }

    private val onRenderStateChangeListener: VGSPDFView.OnRenderStateChangeListener by lazy {
        object : VGSPDFView.OnRenderStateChangeListener {

            override fun onStart(view: VGSPDFView, pages: Int) {}

            override fun onComplete(view: VGSPDFView, pages: Int) {
                analyticsManager.log(
                    RenderContentEvent.createSuccessful(view.getFieldType().toAnalyticTag())
                )
            }

            override fun onError(view: VGSPDFView, t: Throwable) {
                analyticsManager.log(
                    RenderContentEvent.createFailed(view.getFieldType().toAnalyticTag())
                )
            }
        }
    }

    private val onShareDocumentListener: VGSPDFView.OnShareDocumentListener by lazy {
        object : VGSPDFView.OnShareDocumentListener {

            override fun onShare(view: VGSPDFView) {
                analyticsManager.log(ShareContentEvent(view.getFieldType().toAnalyticTag()))
            }
        }
    }

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
    ) : this(context, vaultId, environment, null, null)

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
    ) : this(context, vaultId, environment.toVGSEnvironment(), null, null)

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
            analyticsManager.log(InitEvent(view.getFieldType().toAnalyticTag()))
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
            analyticsManager.log(UnsubscribeFieldEvent(view.getFieldType().toAnalyticTag()))
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
        analyticsManager.isEnabled = isEnabled
        headersStore.isAnalyticsEnabled = isEnabled
    }

    /**
     * Clear all information collected before by VGSShow, cancel all network requests.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        proxyRequestManager.cancelAll()
        analyticsManager.cancelAll()
        listeners.clear()
        clearViewsListeners()
        clearViewsCachedFiles()
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
            analyticsManager.log(
                if (isSuccessful) {
                    CnameValidationEvent.createSuccessful(cname, latency)
                } else {
                    CnameValidationEvent.createFailed(cname, latency)
                }
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
            viewsStore.update((response as? VGSResponse.Success)?.data)
            notifyResponseListeners(response)
        }
    }

    private fun logRequestEvent(request: VGSRequest) {
        val hasCustomData = request.payload != null
        val hasCustomHeaders =
            !request.headers.isNullOrEmpty() || headersStore.getCustom().isNotEmpty()
        analyticsManager.log(
            RequestEvent.createSuccessful(
                hasCustomData,
                hasCustomHeaders,
                hasCustomHostname,
                viewsStore.getViews().any { it is VGSTextView },
                viewsStore.getViews().any { it is VGSPDFView }
            )
        )
    }

    private fun logResponseEvent(response: VGSResponse) {
        val textViewSubscribed = viewsStore.getViews().any { it is VGSTextView }
        val pdfViewSubscribed = viewsStore.getViews().any { it is VGSPDFView }
        analyticsManager.log(
            when (response) {
                is VGSResponse.Success -> ResponseEvent.createSuccessful(
                    response.code,
                    textViewSubscribed,
                    pdfViewSubscribed
                )
                is VGSResponse.Error -> ResponseEvent.createFailed(
                    response.code,
                    textViewSubscribed,
                    pdfViewSubscribed,
                    response.message
                )
            }
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

    private fun clearViewsCachedFiles() {
        if ((context as? Activity)?.isChangingConfigurations == true) {
            return
        }
        viewsStore.getViews().forEach {
            (it as? VGSPDFView)?.clearCachedDocuments()
        }
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
        fun build() = VGSShow(context, id, environment, host, port)
    }
}