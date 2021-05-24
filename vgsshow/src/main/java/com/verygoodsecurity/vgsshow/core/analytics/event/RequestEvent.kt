package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class RequestEvent(
    val status: Status,
    val hasCustomData: Boolean,
    val hasCustomHeaders: Boolean,
    val hasCustomHostname: Boolean,
    val hasText: Boolean,
    val hasPDF: Boolean,
    val code: String = DEFAULT_CODE
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status.value,
            KEY_CONTENT to mutableListOf<String>().apply {
                if (hasCustomData) add(HAS_CUSTOM_DATA)
                if (hasCustomHeaders) add(HAS_CUSTOM_HEADER)
                if (hasCustomHostname) add(HAS_CUSTOM_HOSTNAME)
                if (hasText) add(HAS_TEXT_VIEW)
                if (hasPDF) add(HAS_PDF_VIEW)
            }
        )

    companion object {

        private const val DEFAULT_CODE = "200"

        private const val TYPE = "BeforeSubmit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_CONTENT = "content"

        private const val HAS_CUSTOM_DATA = "custom_data"
        private const val HAS_CUSTOM_HEADER = "custom_header"
        private const val HAS_CUSTOM_HOSTNAME = "custom_hostname"
        private const val HAS_TEXT_VIEW = "text"
        private const val HAS_PDF_VIEW = "pdf"

        fun createSuccessful(
            hasCustomData: Boolean,
            hasCustomHeaders: Boolean,
            hasCustomHostname: Boolean,
            hasText: Boolean,
            hasPDF: Boolean,
        ) = RequestEvent(
            Status.OK,
            hasCustomData,
            hasCustomHeaders,
            hasCustomHostname,
            hasText,
            hasPDF
        )
    }
}