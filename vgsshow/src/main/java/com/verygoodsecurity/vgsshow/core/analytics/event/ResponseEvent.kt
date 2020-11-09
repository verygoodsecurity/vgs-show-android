package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class ResponseEvent(
    val code: String,
    val status: Status,
    val checkSum: String,
    val errorMessage: String? = null
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status.value,
            KEY_CHECK_SUM to checkSum
        ).apply {
            errorMessage?.let {
                put(KEY_ERROR_MESSAGE, errorMessage)
            }
        }

    companion object {

        private const val TYPE = "Submit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_CHECK_SUM = "checkSum"
        private const val KEY_ERROR_MESSAGE = "error"
    }
}