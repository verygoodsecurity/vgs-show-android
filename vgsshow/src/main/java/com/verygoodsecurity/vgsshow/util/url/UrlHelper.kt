package com.verygoodsecurity.vgsshow.util.url

import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.isValid
import com.verygoodsecurity.vgsshow.util.extension.isValidTenantId
import com.verygoodsecurity.vgsshow.util.extension.logWaring


internal object UrlHelper {

    private const val HTTPS_SCHEME = "https://"

    private const val PROXY_URL_DOMEN = "verygoodproxy.com"
    private const val PROXY_URL_DIVIDER = "."
    private const val PROXY_URL_DEFAULT = ""

    fun buildProxyUrl(vaultId: String, environment: VGSEnvironment): String = when {
        !vaultId.isValidTenantId() -> {
            logWaring("VaultId(%s) is not valid", vaultId)
            PROXY_URL_DEFAULT
        }
        !environment.isValid() -> {
            logWaring("Environment(%s) is not valid", environment)
            PROXY_URL_DEFAULT
        }
        else -> StringBuilder(HTTPS_SCHEME)
            .append(vaultId).append(PROXY_URL_DIVIDER)
            .append(environment.value).append(PROXY_URL_DIVIDER)
            .append(PROXY_URL_DOMEN)
            .toString()
    }
}