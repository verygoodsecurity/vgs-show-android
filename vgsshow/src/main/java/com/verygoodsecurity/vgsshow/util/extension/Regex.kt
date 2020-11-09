package com.verygoodsecurity.vgsshow.util.extension

fun String.transformWithRegex(text: String, replacement: String): String {
    return try {
        this.toRegex().replace(text, replacement)
    } catch (ex: Exception) {
        text
    }
}