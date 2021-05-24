package com.verygoodsecurity.vgsshow.core.analytics.event

internal class ShareContentEvent constructor(val field: String) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_FIELD to this.field)

    companion object {

        private const val TYPE = "ContentSharing"

        private const val KEY_FIELD = "field"
    }
}