package com.verygoodsecurity.vgsshow.widget.view.textview.model

import androidx.annotation.IntRange
import kotlin.math.min

/**
 *  Basic data class that represent range from Int.MIN_VALUE to Int.MAX_VALUE.
 *
 *  @property start start of range.
 *  @property end end of range (Inclusive).
 */
data class VGSTextRange constructor(
    @IntRange private val start: Int = 0,
    @IntRange private val end: Int = Int.MAX_VALUE
) {

    fun isValid(textLength: Int): Boolean = end >= 0 && start in 0..min(end, textLength)

    fun getStart(): Int = start

    fun getEnd(textLength: Int): Int = if (end >= textLength) textLength else end.inc()
}