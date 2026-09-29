package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

private const val ANALYTIC_TAG_TEXT = "text"
private const val ANALYTIC_TAG_PDF = "pdf"
private const val ANALYTIC_TAG_IMAGE = "image"

private const val ANALYTIC_UI_XML = "xml"
private const val ANALYTIC_UI_COMPOSE = "compose"

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
 * Maps a [VGSView] to the analytics UI tag for legacy, `AndroidView`-based widgets.
 * @suppress Not for public use.
 */
internal fun VGSView<*>.toAnalyticsUI(): String = ANALYTIC_UI_XML

/**
 * Maps a [BaseState] to the analytics UI tag for native Compose widgets.
 * @suppress Not for public use.
 */
internal fun BaseState.toAnalyticsUI(): String = ANALYTIC_UI_COMPOSE

/**
 * Maps a `VGSTextView.CopyTextFormat` to a `VGSAnalyticsCopyFormat`.
 * @suppress Not for public use.
 */
fun VGSTextView.CopyTextFormat.toAnalyticsFormat() = when(this) {
    VGSTextView.CopyTextFormat.RAW -> VGSAnalyticsCopyFormat.RAW
    VGSTextView.CopyTextFormat.FORMATTED -> VGSAnalyticsCopyFormat.FORMATTED
}