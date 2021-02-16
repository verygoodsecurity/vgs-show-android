package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class CnameValidationEvent constructor(
    val status: Status,
    val host: String,
    val latency: Long
) : Event() {

    override val type: String
        get() = TYPE

    override val customEventAttributes: Map<String, Any>
        get() = mapOf(KEY_STATUS to status.name, KEY_HOST to host, KEY_LATENCY to latency)

    companion object {

        private const val TYPE = "HostNameValidation"

        private const val KEY_STATUS = "status"
        private const val KEY_HOST = "hostname"
        private const val KEY_LATENCY = "latency"

        fun createSuccessful(host: String?, latency: Long): CnameValidationEvent =
            CnameValidationEvent(Status.OK, host ?: "", latency)

        fun createFailed(host: String?, latency: Long): CnameValidationEvent =
            CnameValidationEvent(Status.FAILED, host ?: "", latency)
    }
}