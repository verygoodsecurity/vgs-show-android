package com.verygoodsecurity.vgsshow.core.network.client.extension

private const val URL_SEPARATOR = "/"

internal infix fun String.with(path: String): String {
    return this + if (path.contains(URL_SEPARATOR)) path else "$URL_SEPARATOR$path"
}