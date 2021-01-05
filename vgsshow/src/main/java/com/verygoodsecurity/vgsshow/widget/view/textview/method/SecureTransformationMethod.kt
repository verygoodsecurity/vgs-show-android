package com.verygoodsecurity.vgsshow.widget.view.textview.method

import android.text.method.PasswordTransformationMethod
import android.view.View
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange

internal class SecureTransformationMethod(
    var secureSymbol: Char,
    private val ranges: Array<VGSTextRange>
) : PasswordTransformationMethod() {

    private val regex: Regex = ANY_CHARACTERS_PATTERN.toRegex()

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        var result = source ?: return EMPTY
        for (range in ranges) {
            if (!range.isValid(result.length)) {
                continue
            }
            val first = range.getStart()
            val last = range.getEnd(result.length)
            result = result.replaceRange(first, last, getReplacedPart(first, last, result))
        }
        return result
    }

    private fun getReplacedPart(start: Int, end: Int, source: CharSequence): String =
        source.substring(start, end).replace(regex, secureSymbol.toString())

    companion object {

        private const val EMPTY = ""

        private const val ANY_CHARACTERS_PATTERN = "."
    }
}