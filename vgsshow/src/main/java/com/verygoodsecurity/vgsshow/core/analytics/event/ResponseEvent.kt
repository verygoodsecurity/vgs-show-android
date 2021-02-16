package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class ResponseEvent(
    val isSatelliteMode: Boolean,
    val code: String,
    val status: Status,
    val errorMessage: String? = null
) : Event(isSatelliteMode) {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mutableMapOf(
            KEY_STATUS_CODE to code,
            KEY_STATUS to status.value,
        ).apply {
            errorMessage?.let {
                put(KEY_ERROR_MESSAGE, errorMessage)
            }
        }

    companion object {

        private const val TYPE = "Submit"

        private const val KEY_STATUS_CODE = "statusCode"
        private const val KEY_STATUS = "status"
        private const val KEY_ERROR_MESSAGE = "error"

        fun createSuccessful(isSatelliteMode: Boolean, code: Int): ResponseEvent =
            ResponseEvent(isSatelliteMode, code.toString(), Status.OK)

        fun createFailed(isSatelliteMode: Boolean, code: Int, message: String?): ResponseEvent =
            ResponseEvent(isSatelliteMode, code.toString(), Status.FAILED, message)
    }
}