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
    @IntRange val start: Int = 0,
    @IntRange val end: Int = Int.MAX_VALUE
) {

    /**
     * Check if start and end values is valid.
     *
     * @return true if range valid.
     */
    fun isValid(textLength: Int): Boolean = end >= 0 && start in 0..min(end, textLength)
}