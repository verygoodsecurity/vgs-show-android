package com.verygoodsecurity.vgsshow.widget.extension

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.res.TypedArray
import android.os.PersistableBundle
import android.util.AttributeSet
import androidx.core.content.res.use

internal fun Context.getStyledAttributes(
    attributeSet: AttributeSet?,
    styleArray: IntArray, block: TypedArray.() -> Unit
) = this.obtainStyledAttributes(attributeSet, styleArray).use(block)

private const val CLIP_DATA_CONTENT_SENSITIVE = "android.content.extra.IS_SENSITIVE"

internal fun Context.copyToClipboard(text: String?, label: String = "") {
    val clip = ClipData.newPlainText(label, text ?: "")
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
    ClipDescription.EXTRA_IS_SENSITIVE
    clip.description.extras = PersistableBundle().apply {
        putBoolean(CLIP_DATA_CONTENT_SENSITIVE, true)
    }
}