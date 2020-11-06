package com.verygoodsecurity.vgsshow.core.analytics.event

internal data class RequestEvent(val code: String): Event() {

    override val type: String
        get() = TODO("Not yet implemented")
    override val customEventAttributes: Map<String, Any>
        get() = TODO("Not yet implemented")
}