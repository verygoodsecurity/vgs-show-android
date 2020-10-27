package com.verygoodsecurity.vgsshow.core.network.url

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.util.extension.isValidEnvironment
import com.verygoodsecurity.vgsshow.util.extension.isValidTenantId
import com.verygoodsecurity.vgsshow.util.extension.logDebug

object UrlHelper {

    private const val HTTPS_SCHEME = "https://"

    private const val PROXY_URL_DOMEN = "verygoodproxy.com"
    private const val PROXY_URL_DIVIDER = "."
    private const val PROXY_URL_DEFAULT = ""

    fun buildProxyUrl(vaultId: String, environment: String): String = when {
        !vaultId.isValidTenantId() -> {
            logDebug("Vault id is not valid", VGSShow::class.simpleName)
            PROXY_URL_DEFAULT
        }
        !environment.isValidEnvironment() -> {
            logDebug("Environment is not valid", VGSShow::class.simpleName)
            PROXY_URL_DEFAULT
        }
        else -> StringBuilder(HTTPS_SCHEME)
            .append(vaultId).append(PROXY_URL_DIVIDER)
            .append(environment).append(PROXY_URL_DIVIDER)
            .append(PROXY_URL_DOMEN)
            .toString()
    }
}