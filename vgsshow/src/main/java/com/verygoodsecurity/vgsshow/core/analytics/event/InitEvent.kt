package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class InitEvent constructor(
    val isSatelliteMode: Boolean,
    val field: String
) : Event(isSatelliteMode) {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_FIELD to this.field)

    companion object {

        private const val TYPE = "Init"

        private const val KEY_FIELD = "field"
    }
}