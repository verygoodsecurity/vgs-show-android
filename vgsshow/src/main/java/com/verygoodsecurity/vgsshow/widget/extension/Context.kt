package com.verygoodsecurity.vgsshow.widget.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.TypedArray
import android.os.PersistableBundle
import android.util.AttributeSet
import androidx.core.content.res.use
import com.verygoodsecurity.vgsshow.util.extension.isNougatOrGreater

internal fun Context.getStyledAttributes(
    attributeSet: AttributeSet?,
    styleArray: IntArray, block: TypedArray.() -> Unit
) = this.obtainStyledAttributes(attributeSet, styleArray).use(block)

internal fun Context.copyToClipboard(
    text: String?,
    label: String = "",
    isSensitive: Boolean = false
) {
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(
        ClipData.newPlainText(label, text ?: "").applySensitiveFlagsIfNeeded(isSensitive)
    )
}

private fun ClipData.applySensitiveFlagsIfNeeded(isSensitive: Boolean): ClipData = apply {
    if (isNougatOrGreater && isSensitive) {
        description.extras = PersistableBundle().apply {
            putBoolean("android.content.extra.IS_SENSITIVE", true)
        }
    }
}
