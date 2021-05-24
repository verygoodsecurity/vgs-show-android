package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class ResponseEvent(
    val code: String,
    val status: Status,
    val hasText: Boolean,
    val hasPDF: Boolean,
    val errorMessage: String? = null
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status.value,
            KEY_CONTENT to mutableListOf<String>().apply {
                if (hasText) add(HAS_TEXT_VIEW)
                if (hasPDF) add(HAS_PDF_VIEW)
            }
        ).apply {
            errorMessage?.let {
                put(KEY_ERROR_MESSAGE, errorMessage)
            }
        }

    companion object {

        private const val TYPE = "Submit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_CONTENT = "content"
        private const val KEY_ERROR_MESSAGE = "error"

        private const val HAS_TEXT_VIEW = "text"
        private const val HAS_PDF_VIEW = "pdf"

        fun createSuccessful(code: Int, hasText: Boolean, hasPDF: Boolean) =
            ResponseEvent(code.toString(), Status.OK, hasText, hasPDF)

        fun createFailed(
            code: Int,
            hasText: Boolean,
            hasPDF: Boolean,
            message: String?
        ) = ResponseEvent(code.toString(), Status.FAILED, hasText, hasPDF, message)
    }
}