package com.verygoodsecurity.vgsshow

import android.content.Context
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
import kotlin.concurrent.thread

class VGSShow {

    private val listeners: MutableSet<VGSResponseListener> by lazy { mutableSetOf() }

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
        thread(start = true) {
            while (true) {
                listeners.forEach {

                }
            }
        }
    }

    @WorkerThread
    fun request(fieldName: String, token: String): VGSResponse {
        logDebug("Request{fieldName=$fieldName, token=$token}")
        return proxyNetworkManager.execute(
            VGSRequest.Builder("post", HttpMethod.POST)
                .body(mapOf(fieldName to token))
                .build()
        )
    }

    fun addResponseListener(listener: VGSResponseListener) {
        this.listeners.add(listener)
    }

    fun removeResponseListener(listener: VGSResponseListener) {
        this.listeners.remove(listener)
    }

    fun clearResponseListeners() {
        this.listeners.clear()
    }

    //region Helper methods for testing
    @VisibleForTesting
    internal fun getResponseListeners() = listeners
    //endregion
}