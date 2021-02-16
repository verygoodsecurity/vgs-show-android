package com.verygoodsecurity.vgsshow.core.analytics.event

import com.verygoodsecurity.vgsshow.util.extension.currentTimestamp

internal abstract class Event constructor(private val isSatelliteMode: Boolean) {

    abstract val type: String

    protected abstract val customEventAttributes: Map<String, Any>

    val attributes: Map<String, Any> by lazy {
        HashMap(customEventAttributes).apply {
            put(TYPE, type)
            put(TIMESTAMP, timestamp)
            put(SATELLITE, isSatelliteMode)
        }
    }

    private val timestamp = currentTimestamp

    companion object {

        private const val TYPE = "type"
        private const val TIMESTAMP = "localTimestamp"
        private const val SATELLITE = "vgsSatellite"
    }
}