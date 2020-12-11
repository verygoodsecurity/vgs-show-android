package com.verygoodsecurity.vgsshow.util.extension

import java.net.MalformedURLException
import java.net.URL

private const val SLASH = "/"

internal infix fun String.concatWithSlash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith(SLASH) -> this + suffix
    else -> this + SLASH + suffix
}

private const val DASH = "-"

internal infix fun String.concatWithDash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith(DASH) -> this + suffix
    else -> this + DASH + suffix
}

@Throws(Exception::class)
fun String.toURL(): URL {
    try {
        val url = URL(this)
        url.toURI()
        return url
    } catch (e: Exception) {
        throw MalformedURLException()
    }
}