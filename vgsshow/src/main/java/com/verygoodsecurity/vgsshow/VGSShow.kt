package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toVGSEnvironment
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.cache.CustomHeaderStore
import com.verygoodsecurity.vgsshow.core.network.cache.IVGSCustomHeaderStore
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.extension.toVGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.ConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.getValue
import com.verygoodsecurity.vgsshow.util.url.UrlHelper
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import org.json.JSONException
import org.json.JSONObject

class VGSShow {

    private val listeners: MutableSet<VgsShowResponseListener> by lazy { mutableSetOf() }

    private val viewStore: MutableSet<VGSTextView> by lazy { mutableSetOf() }

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private val customHeadersStore: IVGSCustomHeaderStore

    private val proxyNetworkManager: IHttpRequestManager

    constructor(
        context: Context,
        vaultId: String,
        environment: String
    ) : this(context, vaultId, environment.toVGSEnvironment())

    constructor(context: Context, vaultId: String, environment: VGSEnvironment) {
        customHeadersStore = CustomHeaderStore()
        proxyNetworkManager = HttpRequestManager(
            UrlHelper.buildProxyUrl(vaultId, environment),
            customHeadersStore,
            ConnectionHelper(context)
        )
    }

    @WorkerThread
    fun request(path: String, method: VGSHttpMethod, payload: JSONObject): VGSResponse =
        proxyNetworkManager.execute(VGSRequest.Builder(path, method).body(payload).build()).also {
            mainHandler.post { notifyViews(it) }
        }

    @AnyThread
    fun requestAsync(path: String, method: VGSHttpMethod, payload: JSONObject) {
        proxyNetworkManager.enqueue(VGSRequest.Builder(path, method).body(payload).build()) {
            mainHandler.post {
                notifyViews(it)
                notifyResponseListeners(it)
            }
        }
    }

    fun addResponseListener(listener: VgsShowResponseListener) {
        listeners.add(listener)
    }

    fun removeResponseListener(listener: VgsShowResponseListener) {
        listeners.remove(listener)
    }

    fun clearResponseListeners() {
        listeners.clear()
    }

    fun bindView(view: VGSTextView) {
        viewStore.add(view)
    }

    fun unbindView(view: VGSTextView) {
        viewStore.remove(view)
    }

    /**
     * Used to edit custom request headers
     * @return Custom headers store
     */
    fun getCustomHeadersStore() = customHeadersStore

    //region Helper methods for testing
    @VisibleForTesting
    internal fun getResponseListeners() = listeners

    @VisibleForTesting
    internal fun getViewsStore() = viewStore
    //endregion

    @MainThread
    private fun notifyResponseListeners(response: VGSResponse) {
        listeners.forEach {
            it.onResponse(response)
        }
    }

    // TODO Refactor this method(Single responsibility)
    @MainThread
    private fun notifyViews(response: VGSResponse) {
        if (response !is VGSResponse.Success) {
            return
        }
        try {
            viewStore.forEach { view ->
                JSONObject(response.raw).getValue(view.getFieldName()).let {
                    view.setText(it)
                }
            }
        } catch (t: JSONException) {
            notifyResponseListeners(VGSException.JSONException().toVGSResponse())
        }
    }
}