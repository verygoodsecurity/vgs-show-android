package com.verygoodsecurity.vgsshow.util.extension

import java.security.MessageDigest

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

internal fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
