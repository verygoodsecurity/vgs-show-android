package com.verygoodsecurity.vgsshow.core.analytics.extension

import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val ANALYTIC_TAG_TEXT = "text"
private const val ANALYTIC_TAG_IMAGE = "image" // TODO: Rename

internal fun VGSFieldType.toAnalyticTag() = when (this) {
    VGSFieldType.INFO -> ANALYTIC_TAG_TEXT
    VGSFieldType.IMAGE -> ANALYTIC_TAG_IMAGE
}