package com.verygoodsecurity.vgsshow.core.analytics

import androidx.annotation.AnyThread

internal interface IAnalyticsManager {

    @AnyThread
    fun log(event: Event)

    fun cancelAll()
}