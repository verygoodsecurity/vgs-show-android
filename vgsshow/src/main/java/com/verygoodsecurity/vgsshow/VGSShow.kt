package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
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
import com.verygoodsecurity.vgsshow.core.network.headers.StaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.BaseNetworkConnectionHelper
import com.verygoodsecurity.vgsshow.util.connection.NetworkConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.isValidUrl
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.toHost
import com.verygoodsecurity.vgsshow.util.url.UrlHelper.buildProxyUrl
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
 */
class VGSShow constructor(
    context: Context,
    private val vaultId: String,
    private val environment: VGSEnvironment
) {

    private val listeners: MutableSet<VGSOnResponseListener> by lazy { mutableSetOf() }

    private val viewsStore = ViewsStore()

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val headersStore: StaticHeadersStore

    private val proxyRequestManager: IHttpRequestManager

    private val connectionHelper: NetworkConnectionHelper

    private val analyticsManager: IAnalyticsManager

    private var hasCustomHostname: Boolean = false

    private val onTextCopyListener: VGSTextView.OnTextCopyListener
    private val onSecureTextRangeSetListener: VGSTextView.OnSetSecureTextRangeSetListener

    init {
        headersStore = ProxyStaticHeadersStore()
        connectionHelper = BaseNetworkConnectionHelper(context)
        proxyRequestManager = HttpRequestManager(buildProxyUrl(vaultId, environment), headersStore)
        analyticsManager = AnalyticsManager(vaultId, environment, connectionHelper)
        onTextCopyListener = object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                analyticsManager.log(CopyToClipboardEvent(format))
            }
        }
        onSecureTextRangeSetListener = object : VGSTextView.OnSetSecureTextRangeSetListener {

            override fun onSecureTextRangeSet(view: VGSTextView) {
                analyticsManager.log(
                    SetSecureTextEvent(
                        view.getContentPath(),
                        view.getFieldType().toAnalyticTag()
                    )
                )
            }
        }
    }

    /**
     * Secondary constructor that allows specify environment as string.
     *
     * @param context lifecycle owner context.
     * @param vaultId unique vault id.
     * @param environment type of vault, ex. "sandbox-eu-1"
     */
    constructor(
        context: Context,
        vaultId: String,
        environment: String
    ) : this(context, vaultId, environment.toVGSEnvironment())

    /**
     * Synchronous request for reveal data. Note: This function should be executed in background thread.
     * @throws android.os.NetworkOnMainThreadException if this function executes in main thread.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     * @param payload key-value data
     */
    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun request(path: String, method: VGSHttpMethod, payload: Map<String, Any>): VGSResponse =
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
    @AnyThread
    fun requestAsync(path: String, method: VGSHttpMethod, payload: Map<String, Any>) {
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
     * @param view VGS secure view. @see [com.verygoodsecurity.vgsshow.widget.VGSTextView]
     */
    fun subscribe(view: VGSView<*>) {
        if (viewsStore.add(view)) {
            analyticsManager.log(InitEvent(view.getFieldType().toAnalyticTag()))
            if (view is VGSTextView) {
                handleTextViewSubscribtion(view)
            }
            view.onViewSubscribed()
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
     * Used to enable/disable analytics events.
     *
     * @param isEnabled true if VGSShow should send analytics events.
     */
    fun setAnalyticsEnabled(isEnabled: Boolean) {
        analyticsManager.isEnabled = isEnabled
    }

    /**
     * Clear all information collected before by VGSShow, cancel all network requests.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        proxyRequestManager.cancelAll()
        analyticsManager.cancelAll()
        listeners.clear()
        viewsStore.getViews().forEach {
            (it as? VGSTextView)?.removeOnCopyTextListener(onTextCopyListener)
        }
        viewsStore.clear()
        headersStore.clear()
    }

    //region Helper methods for testing
    @VisibleForTesting
    internal fun getResponseListeners() = listeners

    @VisibleForTesting
    internal fun getViewsStore() = viewsStore
    //endregion

    /**
     * Sets the VGSShow instance to use the custom hostname.
     *
     * @param cname Custom hostname.
     */
    private fun setCname(cname: String?) {
        this.proxyRequestManager.setCname(vaultId, cname) { isSuccessful, latency ->
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

    private fun handleTextViewSubscribtion(view: VGSTextView) {
        view.addOnCopyTextListener(onTextCopyListener)
        view.setOnSecureTextRangeSetListener(onSecureTextRangeSetListener)
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
        val hasFields = !viewsStore.isEmpty()
        val hasHeaders = request.headers?.isNotEmpty() == true || headersStore.containsUserHeaders()
        analyticsManager.log(
            RequestEvent.createSuccessful(
                hasFields,
                hasHeaders,
                hasCustomHostname
            )
        )
    }

    private fun logResponseEvent(response: VGSResponse) {
        analyticsManager.log(
            when (response) {
                is VGSResponse.Success -> ResponseEvent.createSuccessful(response.code)
                is VGSResponse.Error -> ResponseEvent.createFailed(response.code, response.message)
            }
        )
    }

    class Builder constructor(private val context: Context, private val id: String) {

        private var environment: VGSEnvironment = VGSEnvironment.Sandbox()

        private var host: String? = null

        /** Specify Environment for the VGSCollect instance. */
        fun setEnvironment(environment: VGSEnvironment): Builder = this.apply {
            this.environment = environment
        }

        /** Sets the VGSCollect instance to use the custom hostname. */
        fun setHostname(cname: String): Builder {
            if (cname.isValidUrl()) {
                host = cname.toHost()
                if (host != cname) {
                    logDebug("Hostname will be normalized to the $host", VGSShow::class.simpleName)
                }
            }
            return this
        }


        fun build() = VGSShow(context, id, environment).apply {
            host?.let { setCname(it) }
        }
    }
}