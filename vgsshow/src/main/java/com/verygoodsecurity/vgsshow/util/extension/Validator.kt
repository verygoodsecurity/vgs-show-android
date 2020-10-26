package com.verygoodsecurity.vgsshow.util.extension

import java.util.regex.Pattern

private const val TENANT_ID_REGEX = "^[a-zA-Z0-9]+\$"

internal fun String.isValidTenantId() = Pattern.compile(TENANT_ID_REGEX).matcher(this).matches()

private const val ENVIRONMENT_REGEX = "^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$"

internal fun String.isValidEnvironment() =
    Pattern.compile(ENVIRONMENT_REGEX).matcher(this).matches()
