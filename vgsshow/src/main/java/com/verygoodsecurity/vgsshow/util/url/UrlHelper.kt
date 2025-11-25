package com.verygoodsecurity.vgsshow.util.url

import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.isValid
import com.verygoodsecurity.vgsshow.util.extension.isValidPort
import com.verygoodsecurity.vgsshow.util.extension.isValidTenantId
import com.verygoodsecurity.vgsshow.util.extension.logWaring

/**
 * A helper object for building URLs.
 * @suppress Not for public use.
 */
internal object UrlHelper {

    private const val HTTPS_SCHEME = "https://"
    private const val HTTP_SCHEME = "http://"

    private const val PROXY_URL_DOMEN = "verygoodproxy.com"
    private const val PROXY_URL_DIVIDER = "."
    private const val PROXY_PORT_DIVIDER = ":"
    private const val EMPTY = ""

    /**
     * Builds a localhost URL with the given host and port.
     *
     * @param localhost The localhost address.
     * @param port The port number.
     * @return The constructed localhost URL.
     */
    fun buildLocalhostUrl(localhost: String, port: Int?): String {
        val prt = if (!port.isValidPort()) {
            logWaring("Port is not specified")
            EMPTY
        } else {
            "$PROXY_PORT_DIVIDER$port"
        }
        return StringBuilder(HTTP_SCHEME)
            .append(localhost)
            .append(prt)
            .toString()
    }

    /**
     * Builds a VGS proxy URL with the given vault ID and environment.
     *
     * @param vaultId The vault ID.
     * @param environment The VGS environment.
     * @return The constructed proxy URL.
     */
    fun buildProxyUrl(vaultId: String, environment: VGSEnvironment): String = when {
        !vaultId.isValidTenantId() -> {
            logWaring("VaultId($vaultId) is not valid")
            EMPTY
        }
        !environment.isValid() -> {
            logWaring("Environment($environment) is not valid")
            EMPTY
        }
        else -> StringBuilder(HTTPS_SCHEME)
            .append(vaultId).append(PROXY_URL_DIVIDER)
            .append(environment.value).append(PROXY_URL_DIVIDER)
            .append(PROXY_URL_DOMEN)
            .toString()
    }
}