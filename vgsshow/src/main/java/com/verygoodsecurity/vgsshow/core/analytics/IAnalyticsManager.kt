package com.verygoodsecurity.vgsshow.core.analytics

internal interface IAnalyticsManager {

    fun log(event: Event)

    fun cancelAll()
}