package com.verygoodsecurity.vgsshow

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.Environment
import com.verygoodsecurity.vgsshow.core.network.NetworkManager
import com.verygoodsecurity.vgsshow.core.network.client.HttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.util.extension.isValidEnvironment
import com.verygoodsecurity.vgsshow.util.extension.isValidTenantId
import com.verygoodsecurity.vgsshow.util.extension.logDebug

class VGSShow {

    private val networkManager: NetworkManager

    constructor(vaultId: String, environment: Environment) : this(vaultId, environment.rawValue)

    constructor(vaultId: String, environment: String) {
        this.networkManager = NetworkManager(buildBaseUrl(vaultId, environment))
    }

    @WorkerThread
    fun request(fieldName: String, alias: String) {
        logDebug("show{fieldName=$fieldName, alias=$alias}")
        val response = networkManager.execute(
            VGSRequest.Builder("post", HttpMethod.POST)
                .body(mapOf(fieldName to alias))
                .build()
        )
        logDebug(response.toString())
    }

    //TODO: Refactor, move to separate class(Single responsibility)
    private fun buildBaseUrl(vaultId: String, environment: String): String = when {
        !vaultId.isValidTenantId() -> {
            logDebug("Vault id is not valid", VGSShow::class.simpleName)
            DEFAULT_BASE_URL
        }
        !environment.isValidEnvironment() -> {
            logDebug("Environment is not valid", VGSShow::class.simpleName)
            DEFAULT_BASE_URL
        }
        else -> StringBuilder(SCHEME)
            .append(vaultId).append(DIVIDER)
            .append(environment).append(DIVIDER)
            .append(DOMEN)
            .toString()
    }

    companion object {

        private const val DEFAULT_BASE_URL = ""

        private const val SCHEME = "https://"
        private const val DOMEN = "verygoodproxy.com"
        private const val DIVIDER = "."
    }
}