package com.verygoodsecurity.vgsshow.widget.view.textview.method

import android.text.method.PasswordTransformationMethod
import android.view.View
import kotlin.math.min

internal class SecureTransformationMethod(
    private val ranges: Array<VGSSecureTextRange>,
    private val options: VGSSecureTextOptions
) : PasswordTransformationMethod() {

    private val regex: Regex = ANY_CHARACTERS_PATTERN.toRegex()

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        var result = source ?: return EMPTY
        ranges.forEach {
            val first = getFirst(it, result.length)
            val last = getLast(it, result.length)
            result = result.replaceRange(first, last, getReplacedPart(first, last, result))
        }
        return result
    }

    private fun getReplacedPart(start: Int, end: Int, source: CharSequence): String =
        source.substring(start, end).replace(regex, options.secureSymbol.toString())

    private fun getFirst(range: VGSSecureTextRange, length: Int): Int =
        if (range.start in (0..min(range.end, length))) range.start else 0

    private fun getLast(range: VGSSecureTextRange, length: Int): Int =
        if (range.end in range.start..length) range.end else length

    companion object {

        private const val EMPTY = ""

        private const val ANY_CHARACTERS_PATTERN = "."
    }
}