package com.verygoodsecurity.vgsshow.widget.extension

import android.content.res.TypedArray
import android.graphics.Typeface
import androidx.annotation.StyleableRes
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getStringOrThrow
import com.verygoodsecurity.vgsshow.util.extension.isOreoOrGreater

internal fun TypedArray.getFontOrNull(@StyleableRes id: Int): Typeface? = when {
    isOreoOrGreater -> getFont(id)
    else -> getString(id)?.run {
        Typeface.create(this, Typeface.NORMAL)
    }
}

internal fun TypedArray.getFloatOrNull(@StyleableRes id: Int): Float? = try {
    getFloatOrThrow(id)
} catch (e: Exception) {
    null
}

internal fun TypedArray.getChar(@StyleableRes id: Int, default: Char): Char {
    return try {
        getStringOrThrow(id).toCharArray().getOrNull(0) ?: default
    } catch (e: Exception) {
        default
    }
}