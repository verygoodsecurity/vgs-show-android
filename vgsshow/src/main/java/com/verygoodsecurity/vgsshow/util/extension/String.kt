package com.verygoodsecurity.vgsshow.util.extension

import java.net.MalformedURLException
import java.net.URL

private const val SLASH = "/"

/**
 * Concatenates two strings with a slash in between.
 * @suppress Not for public use.
 */
internal infix fun String.concatWithSlash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith(SLASH) -> this + suffix
    else -> this + SLASH + suffix
}

private const val DASH = "-"

/**
 * Concatenates two strings with a dash in between.
 * @suppress Not for public use.
 */
internal infix fun String.concatWithDash(suffix: String): String = when {
    suffix.isEmpty() -> this
    suffix.startsWith(DASH) -> this + suffix
    else -> this + DASH + suffix
}

private const val HTTP_PROTOCOL = "http://"
private const val HTTPS_PROTOCOL = "https://"

/**
 * Prepends the HTTP protocol to a string if it's not already present.
 * @suppress Not for public use.
 */
internal fun String.concatWithHttpProtocol(): String {
    return when {
        startsWith(HTTP_PROTOCOL) -> this
        startsWith(HTTPS_PROTOCOL) -> this
        else -> HTTPS_PROTOCOL + this
    }
}

/**
 * Converts a string to a URL.
 * @suppress Not for public use.
 *
 * @throws MalformedURLException if the string is not a valid URL.
 */
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

/**
 * Extracts the host from a URL string.
 * @suppress Not for public use.
 */
internal fun String.toHost(): String {
    return try {
        this.concatWithHttpProtocol().toURL().host
    } catch (e: MalformedURLException) {
        ""
    }
}

/**
 * Compares the hosts of two URL strings.
 * @suppress Not for public use.
 */
internal infix fun String.equalsHosts(name: String?): Boolean {
    return toHost() == name?.toHost()
}