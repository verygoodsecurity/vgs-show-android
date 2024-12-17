package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.sdk.analytics.model.CopyFormat
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val ANALYTIC_TAG_TEXT = "text"
private const val ANALYTIC_TAG_PDF = "pdf"
private const val ANALYTIC_TAG_IMAGE = "image"

internal fun VGSFieldType.toAnalyticTag(): String = when (this) {
    VGSFieldType.INFO -> ANALYTIC_TAG_TEXT
    VGSFieldType.PDF -> ANALYTIC_TAG_PDF
    VGSFieldType.IMAGE -> ANALYTIC_TAG_IMAGE
}

fun VGSTextView.CopyTextFormat.toAnalyticsFormat() = when(this) {
    VGSTextView.CopyTextFormat.RAW -> CopyFormat.RAW
    VGSTextView.CopyTextFormat.FORMATTED -> CopyFormat.FORMATTED
}