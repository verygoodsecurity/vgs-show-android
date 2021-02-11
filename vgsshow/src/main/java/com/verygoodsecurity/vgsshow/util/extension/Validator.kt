package com.verygoodsecurity.vgsshow.util.extension

import android.util.Patterns
import java.util.regex.Pattern

private const val TENANT_ID_REGEX = "^[a-zA-Z0-9]+\$"

internal fun String.isValidTenantId() = Pattern.compile(TENANT_ID_REGEX).matcher(this).matches()

private const val URL_REGEX = "^(?:https?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\/.-]+)+[\\w\\:]+\$"

internal fun String.isValidUrl(): Boolean {
    return when {
        isBlank() -> false
        else -> Pattern.compile(URL_REGEX).matcher(this).matches()
    }
}

internal fun String.isValidIp(): Boolean {
    return when {
        isNullOrEmpty() -> false
        else -> Patterns.IP_ADDRESS.matcher(this).matches()
    }
}

private const val LOCALHOST_ALIAS = "10.0.2.2"
private const val PRIVATE_NETWORK_IP_PREFIX = "192.168.0."

internal fun String.isIpAllowed() =
    this == LOCALHOST_ALIAS || this.startsWith(PRIVATE_NETWORK_IP_PREFIX)

internal const val PORT_MIN_VALUE = 1L
internal const val PORT_MAX_VALUE = 65353L

internal fun Int?.isValidPort(): Boolean = this != null && this in PORT_MIN_VALUE..PORT_MAX_VALUE