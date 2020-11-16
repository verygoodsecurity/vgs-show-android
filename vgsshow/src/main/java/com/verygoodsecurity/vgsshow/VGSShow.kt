package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.NetworkOnMainThreadException
import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toVGSEnvironment
import com.verygoodsecurity.vgsshow.core.analytics.AnalyticsManager
import com.verygoodsecurity.vgsshow.core.analytics.IAnalyticsManager
import com.verygoodsecurity.vgsshow.core.analytics.event.InitEvent
import com.verygoodsecurity.vgsshow.core.analytics.event.RequestEvent
import com.verygoodsecurity.vgsshow.core.analytics.event.ResponseEvent
import com.verygoodsecurity.vgsshow.core.analytics.event.Status
import com.verygoodsecurity.vgsshow.core.analytics.extension.toAnalyticTag
import com.verygoodsecurity.vgsshow.core.helper.ViewsStore
import com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager.Companion.NETWORK_RESPONSE_CODES
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.headers.IVGSStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.headers.ProxyStaticHeadersStore
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.ConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.toMD5
import com.verygoodsecurity.vgsshow.util.url.UrlHelper
import com.verygoodsecurity.vgsshow.widget.VGSView
import org.json.JSONObject

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
class VGSShow constructor(context: Context, vaultId: String, environment: VGSEnvironment) {

    private val listeners: MutableSet<VgsShowResponseListener> by lazy { mutableSetOf() }

    private val viewsStore = ViewsStore()

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private val headersStore: IVGSStaticHeadersStore

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

    /**
     * Synchronous request for reveal data. Note: This function should be executed in background thread.
     * @throws android.os.NetworkOnMainThreadException if this function executes in main thread.
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     * @param payload TODO: implement payload as raw string or sealed class and add comment
     */
    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun request(path: String, method: VGSHttpMethod, payload: JSONObject): VGSResponse =
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
        val response = proxyRequestManager.execute(request)
        with(request.payload.toString().toMD5()) {
            logRequestEvent(request, response, this)
            logResponseEvent(response, this)
        }
        mainHandler.post { viewsStore.update((response as? VGSResponse.Success)?.data) }
        return response
    }

    /**
     * Asynchronous request for reveal data
     *
     * @param path path for a request.
     * @param method HTTP method of request. @see [com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod]
     * @param payload TODO: implement payload as raw string or sealed class and add comment
     */
    @AnyThread
    fun requestAsync(path: String, method: VGSHttpMethod, payload: JSONObject) {
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
            with(request.payload.toString().toMD5()) {
                logRequestEvent(request, it, this)
                logResponseEvent(it, this)
            }
            mainHandler.post {
                viewsStore.update((it as? VGSResponse.Success)?.data)
                notifyResponseListeners(it)
            }
        }
    }

    /**
     * Adds a listener to the list of those whose methods are called whenever the VGSShow receive response from Server.
     *
     * @param listener Interface definition for a receiving callback. @see[com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener]
     */
    fun addResponseListener(listener: VgsShowResponseListener) {
        listeners.add(listener)
    }

    /**
     * Clear specific listener attached before.
     *
     * @param listener Interface definition for a receiving callback. @see[com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener]
     */
    fun removeResponseListener(listener: VgsShowResponseListener) {
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
    fun subscribeView(view: VGSView<View>) {
        analyticsManager.log(InitEvent(view.getViewType().toAnalyticTag()))
        viewsStore.add(view)
    }

    /**
     * Remove previously added VGS secure view
     *
     * @param view VGS secure view. @see [com.verygoodsecurity.vgsshow.widget.VGSTextView]
     */
    fun unsubscribeView(view: VGSView<View>) {
        viewsStore.remove(view)
    }

    /**
     * Used to edit static request headers that will be added to all requests of this VGSShow instance.
     *
     * @return Static headers store. @see [com.verygoodsecurity.vgsshow.core.network.headers.IVGSStaticHeadersStore]
     */
    fun getStaticHeadersStore(): IVGSStaticHeadersStore = headersStore

    /**
     * Clear all information collected before by VGSShow, cancel all network requests.
     * Preferably call it inside onDestroy system's callback.
     */
    fun onDestroy() {
        proxyRequestManager.cancelAll()
        analyticsManager.cancelAll()
        listeners.clear()
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

    private fun logRequestEvent(request: VGSRequest, response: VGSResponse, checksum: String) {
        analyticsManager.log(
            when (response.code) {
                in NETWORK_RESPONSE_CODES -> RequestEvent(
                    Status.OK,
                    checksum,
                    !viewsStore.isEmpty(),
                    (request.headers?.isNotEmpty() == true || headersStore.containsUserHeaders())
                )
                else -> RequestEvent(
                    Status.FAILED,
                    checksum,
                    !viewsStore.isEmpty(),
                    (request.headers?.isNotEmpty() == true || headersStore.containsUserHeaders()),
                    response.code.toString()
                )
            }
        )
    }

    private fun logResponseEvent(response: VGSResponse, checksum: String) {
        if (response.code !in NETWORK_RESPONSE_CODES) {
            return
        }
        analyticsManager.log(
            when (response) {
                is VGSResponse.Success -> ResponseEvent(
                    response.code.toString(),
                    Status.OK,
                    checksum
                )
                is VGSResponse.Error -> ResponseEvent(
                    response.code.toString(),
                    Status.FAILED,
                    checksum,
                    response.message
                )
            }
        )
    }
}