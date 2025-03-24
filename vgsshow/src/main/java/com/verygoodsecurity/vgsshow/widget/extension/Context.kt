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

private const val EMPTY_LABEL = ""

internal fun Context.copyToClipboard(text: String?, isSensitive: Boolean) {
    val clipData = ClipData.newPlainText(EMPTY_LABEL, text ?: "").apply {
        if (isNougatOrGreater) {
            description.extras = PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", isSensitive)
            }
        }
    }
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clipData)
}