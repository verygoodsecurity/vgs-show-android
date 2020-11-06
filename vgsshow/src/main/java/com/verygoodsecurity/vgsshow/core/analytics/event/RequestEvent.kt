package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class RequestEvent(
    val status: Status,
    val checkSum: String,
    val hasFields: Boolean,
    val hasStaticHeaders: Boolean,
    val code: String = DEFAULT_CODE
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status,
            KEY_CHECK_SUM to checkSum,
            KEY_CONTENT to mutableListOf<String>().apply {
                if (hasFields) add(HAS_FIELDS)
                if (hasStaticHeaders) add(HAS_STATIC_HEADERS)
            }
        )

    companion object {

        private const val DEFAULT_CODE = "200"

        private const val TYPE = "BeforeSubmit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_CHECK_SUM = "checkSum"
        private const val KEY_CONTENT = "content"

        private const val HAS_FIELDS = "fields"
        private const val HAS_STATIC_HEADERS = "customHeaders"
    }
}