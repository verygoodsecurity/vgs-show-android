package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class UnsubscribeFieldEvent constructor(private val field: String) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_FIELD to this.field)

    companion object {

        private const val TYPE = "UnsubscribeField"

        private const val KEY_FIELD = "field"
    }
}