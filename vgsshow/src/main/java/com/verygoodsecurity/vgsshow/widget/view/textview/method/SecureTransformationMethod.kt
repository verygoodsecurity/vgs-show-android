package com.verygoodsecurity.vgsshow.widget.view.textview.method

import android.text.method.PasswordTransformationMethod
import android.view.View
import com.verygoodsecurity.vgsshow.util.extension.logDebug
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
                logDebug("A specified range[${range.start}, ${range.start}] was not correct. It will be skipped.")
                continue
            }
            result = replaceRange(range, result)
        }
        return result
    }

    private fun replaceRange(range: VGSTextRange, source: CharSequence): CharSequence {
        val first = range.start
        val last = if (range.end > source.length) source.length else range.end.inc()
        val replacePart = source.substring(first, last).replace(regex, secureSymbol.toString())
        return source.replaceRange(first, last, replacePart)
    }

    companion object {

        private const val EMPTY = ""

        private const val ANY_CHARACTERS_PATTERN = "."
    }
}