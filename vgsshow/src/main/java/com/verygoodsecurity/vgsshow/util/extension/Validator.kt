package com.verygoodsecurity.vgsshow.util.extension

import java.net.URL
import java.util.regex.Pattern

private const val TENANT_ID_REGEX = "^[a-zA-Z0-9]+\$"

internal fun String.isValidTenantId() = Pattern.compile(TENANT_ID_REGEX).matcher(this).matches()

internal fun String.isValidUrl() = try {
    URL(this).toURI()
    true
} catch (e: Exception) {
    false
}
