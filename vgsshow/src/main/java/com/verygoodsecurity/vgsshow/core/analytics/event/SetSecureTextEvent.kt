package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class SetSecureTextEvent constructor(
    val isSatelliteMode: Boolean,
    val contentPath: String,
    val field: String
) : Event(isSatelliteMode) {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(
            KEY_CONTENT_PATH to this.contentPath,
            KEY_FIELD to this.field
        )

    companion object {

        private const val TYPE = "SetSecureTextRange"

        private const val KEY_CONTENT_PATH = "contentPath"
        private const val KEY_FIELD = "field"
    }
}