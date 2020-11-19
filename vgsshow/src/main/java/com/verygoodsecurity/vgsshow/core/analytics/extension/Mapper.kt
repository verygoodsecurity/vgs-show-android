package com.verygoodsecurity.vgsshow.core.analytics.extension

import com.verygoodsecurity.vgsshow.widget.core.VGSViewType

private const val ANALYTIC_TAG_TEXT = "text"

internal fun VGSViewType.toAnalyticTag() = when (this) {
    VGSViewType.INFO -> ANALYTIC_TAG_TEXT
}