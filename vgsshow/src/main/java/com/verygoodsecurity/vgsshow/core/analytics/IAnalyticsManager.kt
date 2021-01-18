package com.verygoodsecurity.vgsshow.core.analytics

import com.verygoodsecurity.vgsshow.core.analytics.event.Event

internal interface IAnalyticsManager {

    var isEnabled: Boolean

    fun log(event: Event)

    fun cancelAll()
}