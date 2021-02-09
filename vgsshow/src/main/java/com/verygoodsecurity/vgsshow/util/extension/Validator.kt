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
        isBlank() -> false
        else -> Patterns.IP_ADDRESS.matcher(this).matches()
    }
}