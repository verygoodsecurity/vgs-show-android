package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class RequestEvent(
    val isSatelliteMode: Boolean,
    val status: Status,
    val hasFields: Boolean,
    val hasHeaders: Boolean,
    val hasCustomHostname: Boolean,
    val code: String = DEFAULT_CODE
) : Event(isSatelliteMode) {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status.value,
            KEY_CONTENT to mutableListOf<String>().apply {
                if (hasFields) add(HAS_FIELDS)
                if (hasHeaders) add(HAS_HEADERS)
                if (hasCustomHostname) add(HAS_CUSTOM_HOSTNAME)
            }
        )

    companion object {

        private const val DEFAULT_CODE = "200"

        private const val TYPE = "BeforeSubmit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_CONTENT = "content"

        private const val HAS_FIELDS = "fields"
        private const val HAS_HEADERS = "customHeaders"
        private const val HAS_CUSTOM_HOSTNAME = "customHostName"

        fun createSuccessful(
            isSatelliteMode: Boolean,
            hasFields: Boolean,
            hasHeaders: Boolean,
            hasCustomHostname: Boolean
        ) = RequestEvent(isSatelliteMode, Status.OK, hasFields, hasHeaders, hasCustomHostname)
    }
}