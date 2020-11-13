package com.verygoodsecurity.vgsshow.widgetnew.extension

import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import androidx.core.content.res.getColorOrThrow

internal fun TypedArray.getColorOrNull(@StyleableRes id: Int): Int? {
    return try {
        this.getColorOrThrow(id)
    } catch (e: IllegalStateException) {
        null
    }
}