package com.verygoodsecurity.vgsshow.widget.view.textview.method

import android.text.method.PasswordTransformationMethod
import android.view.View
import kotlin.math.min

internal class SecureTransformationMethod(
    private val range: IntRange,
    private val options: VGSSecureTextOptions
) : PasswordTransformationMethod() {

    private val regex: Regex = getPattern(options.ignoreTransformationRegex).toRegex()

    private var first: Int = range.first
    private var last: Int = range.last

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        first = getFirst(source?.length ?: 0)
        last = getLast(source?.length ?: 0)
        return source?.replaceRange(first, last, getReplacedPart(source)) ?: EMPTY
    }

    private fun getReplacedPart(source: CharSequence): String =
        source.substring(first, last).replace(regex, options.secureSymbol.toString())

    private fun getPattern(ignoreTransformRegex: Boolean) =
        if (ignoreTransformRegex) ONLY_LETTERS_AND_NUMBERS_PATTERN else ANY_CHARACTERS_PATTERN

    private fun getFirst(length: Int): Int =
        if (range.first in (0..min(range.last, length))) range.first else 0

    private fun getLast(length: Int): Int =
        if (range.last in range.first..length) range.last else length

    companion object {

        private const val EMPTY = ""

        private const val ANY_CHARACTERS_PATTERN = "."
        private const val ONLY_LETTERS_AND_NUMBERS_PATTERN = "[a-zA-Z0-9]"
    }
}