package com.verygoodsecurity.vgsshow.widget.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.core.content.res.use

fun Context.getStyledAttributes(
    attributeSet: AttributeSet?,
    styleArray: IntArray, block: TypedArray.() -> Unit
) = this.obtainStyledAttributes(attributeSet, styleArray).use(block)

internal fun Context.copyToClipboard(text: String?, label: String = "") {
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
        ClipData.newPlainText(label, text ?: "")
    )
}