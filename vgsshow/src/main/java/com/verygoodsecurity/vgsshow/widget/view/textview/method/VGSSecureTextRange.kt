package com.verygoodsecurity.vgsshow.widget.view.textview.method

import androidx.annotation.IntRange

/**
 *  Basic data class that represent range from Long.MIN_VALUE to Long.MAX_VALUE.
 *
 *  @property start start of range.
 *  @property end end of range.
 */
data class VGSSecureTextRange constructor(
    @IntRange val start: Int,
    @IntRange val end: Int
)