package com.verygoodsecurity.vgsshow.core.analytics.event

import com.verygoodsecurity.vgsshow.widget.VGSTextView
import java.util.*

internal data class CopyToClipboardEvent constructor(
    val field: String,
    val format: VGSTextView.CopyTextFormat,
    val status: String = DEFAULT_STATUS
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(
            KEY_FIELD to this.field,
            KEY_STATUS to status,
            KEY_FORMAT to format.name.toLowerCase(Locale.US)
        )

    companion object {

        private const val TYPE = "Copy to clipboard click"

        private const val KEY_FIELD = "field"
        private const val KEY_FORMAT = "copy_format"
        private const val KEY_STATUS = "status"
        private const val DEFAULT_STATUS = "Clicked"
    }
}