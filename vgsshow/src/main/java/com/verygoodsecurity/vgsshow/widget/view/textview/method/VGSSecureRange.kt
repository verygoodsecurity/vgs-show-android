package com.verygoodsecurity.vgsshow.widget.view.textview.method

import androidx.annotation.IntRange

data class VGSSecureRange constructor(
    @IntRange val start: Int,
    @IntRange val end: Int
)