package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class InitEvent constructor(
    val field: String,
    val contentPath: String
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_FIELD to this.field, KEY_CONTENT_PATH to contentPath)

    companion object {

        private const val TYPE = "Init"

        private const val KEY_FIELD = "field"
        private const val KEY_CONTENT_PATH = "contentPath"
    }
}