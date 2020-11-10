package com.verygoodsecurity.vgsshow.core.analytics.extension

import com.verygoodsecurity.vgsshow.widget.view.ViewType

private const val ANALYTIC_TAG_TEXT = "text"

internal fun ViewType.toAnalyticTag() = when (this) {
    ViewType.TEXT -> ANALYTIC_TAG_TEXT
}