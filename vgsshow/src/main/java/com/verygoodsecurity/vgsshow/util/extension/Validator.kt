package com.verygoodsecurity.vgsshow.util.extension

import java.util.regex.Pattern

private const val TENANT_ID_REGEX = "^[a-zA-Z0-9]+\$"

internal fun String.isValidTenantId() = Pattern.compile(TENANT_ID_REGEX).matcher(this).matches()

private const val URL_REGEX = "^(?:https?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\/.-]+)+[\\w\\:]+\$"

internal fun String.isValidUrl(): Boolean {
    return when {
        isNullOrBlank() -> false
        else -> Pattern.compile(URL_REGEX).matcher(this).matches()
    }
}
