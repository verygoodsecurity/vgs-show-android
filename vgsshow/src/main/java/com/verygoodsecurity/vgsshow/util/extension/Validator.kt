package com.verygoodsecurity.vgsshow.util.extension

import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

private const val TENANT_ID_REGEX = "^[a-zA-Z0-9]+\$"

internal fun String.isValidTenantId() = Pattern.compile(TENANT_ID_REGEX).matcher(this).matches()

internal fun String.isValidUrl(): Boolean = when {
    isBlank() -> false
    else -> PatternsCompat.WEB_URL.matcher(this).matches()
}

internal fun String.isValidIp(): Boolean = when {
    isNullOrEmpty() -> false
    else -> PatternsCompat.IP_ADDRESS.matcher(this).matches()
}

private const val AVD_LOCALHOST_ALIAS = "10.0.2.2"
private const val GENYMOTION_LOCALHOST_ALIAS = "10.0.3.2"
private const val PRIVATE_NETWORK_IP_PREFIX = "192.168."

internal fun String.isIpAllowed() = this == AVD_LOCALHOST_ALIAS ||
        this == GENYMOTION_LOCALHOST_ALIAS ||
        this.startsWith(PRIVATE_NETWORK_IP_PREFIX)

internal const val PORT_MIN_VALUE = 1L
internal const val PORT_MAX_VALUE = 65353L

internal fun Int?.isValidPort(): Boolean = this != null && this in PORT_MIN_VALUE..PORT_MAX_VALUE