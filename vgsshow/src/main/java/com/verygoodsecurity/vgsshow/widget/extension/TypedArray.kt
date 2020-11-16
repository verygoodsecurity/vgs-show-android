package com.verygoodsecurity.vgsshow.widget.extension

import android.content.res.TypedArray
import android.graphics.Typeface
import androidx.annotation.StyleableRes
import androidx.core.content.res.getFloatOrThrow
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.util.extension.isOreoOrGreater

internal fun TypedArray.getFontOrNull(@StyleableRes id: Int): Typeface? = when {
    isOreoOrGreater -> getFont(R.styleable.VGSTextView_fontFamily)
    else -> getString(R.styleable.VGSTextView_fontFamily)?.run {
        Typeface.create(this, Typeface.NORMAL)
    }
}

internal fun TypedArray.getFloatOrNull(@StyleableRes id: Int): Float? = try {
    getFloatOrThrow(id)
} catch (e: Exception) {
    null
}