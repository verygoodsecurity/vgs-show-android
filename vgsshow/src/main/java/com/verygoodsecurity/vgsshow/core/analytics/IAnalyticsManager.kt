package com.verygoodsecurity.vgsshow.core.analytics

import com.verygoodsecurity.vgsshow.core.analytics.event.Event

internal interface IAnalyticsManager {

    fun log(event: Event)

    fun cancelAll()
}