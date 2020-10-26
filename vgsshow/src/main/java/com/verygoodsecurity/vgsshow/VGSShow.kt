package com.verygoodsecurity.vgsshow

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.Environment
import com.verygoodsecurity.vgsshow.core.network.NetworkManager
import com.verygoodsecurity.vgsshow.core.network.client.Method
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.util.extension.logDebug

class VGSShow {

    private val networkManager: NetworkManager

    constructor(vaultId: String, environment: Environment) : this(vaultId, environment.rawValue)

    constructor(vaultId: String, environment: String) {
        this.networkManager = NetworkManager(vaultId, environment)
    }

    @WorkerThread
    fun request(fieldName: String, alias: String) {
        logDebug("show{fieldName=$fieldName, alias=$alias}")
        val response = networkManager.execute(
            VGSRequest.Builder("post", Method.POST)
                .body(mapOf(fieldName to alias))
                .build()
        )
        logDebug(response.toString())
    }
}