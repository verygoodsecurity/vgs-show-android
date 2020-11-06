package com.verygoodsecurity.vgsshow.util.extension

import java.util.regex.PatternSyntaxException

fun String.transformWithRegex(text: String, replacement: String): String {
    return try {
        this.toRegex().replace(text, replacement)
    } catch (ex: PatternSyntaxException) {
        text
    } catch (ex: IndexOutOfBoundsException) {
        text
    }
}