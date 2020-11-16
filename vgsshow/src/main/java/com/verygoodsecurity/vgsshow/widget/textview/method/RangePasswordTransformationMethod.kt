package com.verygoodsecurity.vgsshow.widget.textview.method

import android.text.method.PasswordTransformationMethod
import android.view.View

internal class RangePasswordTransformationMethod(
    private val start: Int = -1,
    private val end: Int = -1
) : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        val passRange = calculateRange(source)

        return source?.substring(passRange.first, passRange.second)?.run {
            val newStr = this.replace(".".toRegex(), "•")
            source.replaceRange(passRange.first, passRange.second, newStr)
        } ?: super.getTransformation(source, view)
    }

    private fun calculateRange(source: CharSequence?): Pair<Int, Int> {
        val positionEnd = if (end <= -1) source?.length ?: 0 else end
        val positionStart = if (start <= -1) 0 else start

        return Pair(
            begin(positionStart, positionEnd, source),
            end(positionStart, positionEnd, source)
        )
    }

    private fun begin(start: Int, end: Int, source: CharSequence?): Int {
        return when {
            start >= source?.length ?: 0 -> 0
            start > end -> 0
            else -> start
        }
    }

    private fun end(start: Int, end: Int, source: CharSequence?): Int {
        return (source?.length ?: 0).run {
            when {
                end < start -> this
                end >= this -> this
                else -> end
            }
        }
    }
}