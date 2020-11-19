package com.verygoodsecurity.vgsshow.core.analytics.extension

import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val ANALYTIC_TAG_TEXT = "text"

internal fun VGSFieldType.toAnalyticTag() = when (this) {
    VGSFieldType.INFO -> ANALYTIC_TAG_TEXT
}