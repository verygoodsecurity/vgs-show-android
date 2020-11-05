package com.verygoodsecurity.vgsshow.core.analytics

sealed class Event {

    abstract val attributes: MutableMap<String, Any>
}