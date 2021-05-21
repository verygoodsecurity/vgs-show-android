package com.verygoodsecurity.vgsshow.core.analytics.event

internal class RenderContentEvent(
    val field: String,
    val status: Status,
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_FIELD to this.field, KEY_STATUS to status.value)

    companion object {

        private const val TYPE = "ContentRendering"

        private const val KEY_FIELD = "field"
        private const val KEY_STATUS = "status"

        fun createSuccessful(field: String) = RenderContentEvent(field, Status.OK)

        fun createFailed(field: String) = RenderContentEvent(field, Status.FAILED)
    }
}