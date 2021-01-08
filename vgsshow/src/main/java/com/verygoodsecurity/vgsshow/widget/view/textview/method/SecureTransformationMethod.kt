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
            if(result.isNotEmpty()) {
                result = replaceRange(range, result)
            }
        }
        return result
    }

    private fun replaceRange(range: VGSTextRange, source: CharSequence): CharSequence {
        val first = range.start
        val last = if (range.end > source.length) source.length else range.end.inc()

        return source.replaceRange(
            first,
            last,
            getReplacedPart(
                first,
                last,
                source
            )
        )
    }

    private fun getReplacedPart(start: Int, end: Int, source: CharSequence): String {
        return if (end > source.length) {
            source.substring(start, source.length).replace(regex, secureSymbol.toString())
        } else {
            source.substring(start, end).replace(regex, secureSymbol.toString())
        }
    }

    companion object {

        private const val EMPTY = ""

        private const val ANY_CHARACTERS_PATTERN = "."
    }
}