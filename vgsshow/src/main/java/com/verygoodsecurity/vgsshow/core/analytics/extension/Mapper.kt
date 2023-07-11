package com.verygoodsecurity.vgsshow.core.analytics.extension

import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val ANALYTIC_TAG_TEXT = "text"
private const val ANALYTIC_TAG_PDF = "pdf"
private const val ANALYTIC_TAG_IMAGE = "image"

internal fun VGSFieldType.toAnalyticTag(): String = when (this) {
    VGSFieldType.INFO -> ANALYTIC_TAG_TEXT
    VGSFieldType.PDF -> ANALYTIC_TAG_PDF
    VGSFieldType.IMAGE -> ANALYTIC_TAG_IMAGE
}