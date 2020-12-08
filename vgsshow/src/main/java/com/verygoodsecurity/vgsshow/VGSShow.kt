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
import com.verygoodsecurity.vgsshow.core.helper.ViewsStore
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager.Companion.NETWORK_RESPONSE_CODES
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.headers.VGSStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.headers.ProxyStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.ConnectionHelper
import com.verygoodsecurity.vgsshow.util.url.UrlHelper
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 * Allows reveal secure data into secure views.
 * Entry-point into Show SDK.
 *
 * @constructor create configured, ready to use entry-point into Show SDK.
 * @param context lifecycle owner context.
 * @param vaultId unique vault id.
 * @param environment type of vault. @see [com.verygoodsecurity.vgsshow.core.VGSEnvironment]
 *
 * @since 1.0.0
 */
class VGSShow constructor(
    context: Context,
    vaultId: String,
    environment: VGSEnvironment
) : VGSTextView.OnTextCopyListener {

    private val listeners: MutableSet<VGSOnResponseListener> by lazy { mutableSetOf() }

    private val viewsStore = ViewsStore()

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val headersStore: VGSStaticHeadersStore

    private val proxyRequestManager: IHttpRequestManager

    private val analyticsManager: IAnalyticsManager

    init {
        headersStore = ProxyStaticHeadersStore()
        val connectionHelper = ConnectionHelper(context)
        proxyRequestManager = HttpRequestManager(
            UrlHelper.buildProxyUrl(vaultId, environment),
            headersStore,
            connectionHelper
        )
        analyticsManager = AnalyticsManager(vaultId, environment, connectionHelper)
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

    override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
        analyticsManager.log(CopyToClipboardEvent(format))
    }

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
        return with(proxyRequestManager.execute(request)) {
            logRequestEvent(request, this)
            logResponseEvent(this)
            mainHandler.post { viewsStore.update((this as? VGSResponse.Success)?.data) }
            this
        }
    }

    /**
     * Asynchronous request for reveal data
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
        proxyRequestManager.enqueue(request) {
            logRequestEvent(request, it)
            logResponseEvent(it)
            mainHandler.post {
                viewsStore.update((it as? VGSResponse.Success)?.data)
                notifyResponseListeners(it)
            }
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
                view.addOnCopyTextListener(this)
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
                view.removeOnCopyTextListener(this)
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
     * Clear all information collected before by VGSShow, cancel all network requests.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        proxyRequestManager.cancelAll()
        analyticsManager.cancelAll()
        listeners.clear()
        viewsStore.getViews().forEach { (it as? VGSTextView)?.removeOnCopyTextListener(this) }
        viewsStore.clear()
        headersStore.clear()
    }

    //region Helper methods for testing
    @VisibleForTesting
    internal fun getResponseListeners() = listeners

    @VisibleForTesting
    internal fun getViewsStore() = viewsStore
    //endregion

    @MainThread
    private fun notifyResponseListeners(response: VGSResponse) {
        listeners.forEach {
            it.onResponse(response)
        }
    }

    private fun logRequestEvent(request: VGSRequest, response: VGSResponse) {
        val hasFields = !viewsStore.isEmpty()
        val hasHeaders = request.headers?.isNotEmpty() == true || headersStore.containsUserHeaders()
        analyticsManager.log(
            when (response.code) {
                in NETWORK_RESPONSE_CODES -> RequestEvent.createSuccessful(hasFields, hasHeaders)
                else -> RequestEvent.createFailed(hasFields, hasHeaders, response.code)
            }
        )
    }

    private fun logResponseEvent(response: VGSResponse) {
        if (response.code !in NETWORK_RESPONSE_CODES) {
            return
        }
        analyticsManager.log(
            when (response) {
                is VGSResponse.Success -> ResponseEvent.createSuccessful(response.code)
                is VGSResponse.Error -> ResponseEvent.createFailed(response.code, response.message)
            }
        )
    }
}