package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.Environment
import com.verygoodsecurity.vgsshow.core.listener.VGSResponseListener
import com.verygoodsecurity.vgsshow.core.network.HttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.IHttpRequestManager
import com.verygoodsecurity.vgsshow.core.network.client.HttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.connection.ConnectionHelper
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.url.UrlHelper
import com.verygoodsecurity.vgsshow.widget.VGSTextView

class VGSShow {

    private val listeners: MutableSet<VGSResponseListener> by lazy { mutableSetOf() }

    private val viewStore: MutableSet<VGSTextView> by lazy { mutableSetOf() }

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private val proxyNetworkManager: IHttpRequestManager

    constructor(
        context: Context,
        vaultId: String,
        environment: Environment
    ) : this(context, vaultId, environment.rawValue)

    constructor(context: Context, vaultId: String, environment: String) {
        this.proxyNetworkManager = HttpRequestManager(
            UrlHelper.buildProxyUrl(vaultId, environment),
            ConnectionHelper(context)
        )
    }

    @WorkerThread
    fun request(fieldName: String, token: String) {
        logDebug("Request{fieldName=$fieldName, token=$token}")
        val response = proxyNetworkManager.execute(
            VGSRequest.Builder("post", HttpMethod.POST)
                .body(mapOf(fieldName to token))
                .build()
        )
        mainHandler.post {
            notifyResponseListeners(response)
            notifyViews(fieldName, response)
        }
    }

    fun addResponseListener(listener: VGSResponseListener) {
        listeners.add(listener)
    }

    fun removeResponseListener(listener: VGSResponseListener) {
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

    //region Helper methods for testing
    @VisibleForTesting
    internal fun getResponseListeners() = listeners
    //endregion

    @MainThread
    private fun notifyResponseListeners(response: VGSResponse) {
        listeners.forEach {
            it.onResponse(response)
        }
    }

    @MainThread
    private fun notifyViews(fieldName: String, response: VGSResponse) {
        if (response !is VGSResponse.Success) {
            return
        }
        // TODO: implement view update correct(Current implementation is just for testing)
        ((response.data?.get("response") as? Map<*, *>)?.get("data") as? String)?.let {
            viewStore.find { view -> view.getFieldName() == fieldName }?.setText(it)
        }
    }
}