package com.verygoodsecurity.vgsshow.util.extension

import java.net.URL

private const val URL_SEPARATOR = "/"

internal infix fun String.with(path: String): String {
    return this + if (path.contains(URL_SEPARATOR)) path else "$URL_SEPARATOR$path"
}

internal fun String.isValidUrl() = try {
    URL(this).toURI()
    true
} catch (e: Exception) {
    false
}