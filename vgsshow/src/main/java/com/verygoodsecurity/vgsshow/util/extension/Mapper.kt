package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val ANALYTIC_TAG_TEXT = "text"
private const val ANALYTIC_TAG_PDF = "pdf"
private const val ANALYTIC_TAG_IMAGE = "image"

private const val ANALYTIC_UI_XML = "xml"
private const val ANALYTIC_UI_COMPOSE = "compose"
private const val ANALYTIC_UI_NATIVE_COMPOSE = "native-compose"

/**
 * Maps a [VGSFieldType] to an analytics tag.
 * @suppress Not for public use.
 */
internal fun VGSFieldType.toAnalyticTag(): String = when (this) {
    VGSFieldType.INFO -> ANALYTIC_TAG_TEXT
    VGSFieldType.PDF -> ANALYTIC_TAG_PDF
    VGSFieldType.IMAGE -> ANALYTIC_TAG_IMAGE
}

/**
 * Analytics UI tag of a [VGSView]: `compose` when it is hosted by an `AndroidView` wrapper,
 * `xml` otherwise. Matches the tags used by VGS Collect.
 * @suppress Not for public use.
 */
internal fun analyticsUI(isCompose: Boolean): String =
    if (isCompose) ANALYTIC_UI_COMPOSE else ANALYTIC_UI_XML

/**
 * Analytics UI tag of native Compose widgets (states), matching VGS Collect.
 * @suppress Not for public use.
 */
internal fun nativeComposeAnalyticsUI(): String = ANALYTIC_UI_NATIVE_COMPOSE

/**
 * Maps a `VGSTextView.CopyTextFormat` to a `VGSAnalyticsCopyFormat`.
 * @suppress Not for public use.
 */
fun VGSTextView.CopyTextFormat.toAnalyticsFormat() = when(this) {
    VGSTextView.CopyTextFormat.RAW -> VGSAnalyticsCopyFormat.RAW
    VGSTextView.CopyTextFormat.FORMATTED -> VGSAnalyticsCopyFormat.FORMATTED
}