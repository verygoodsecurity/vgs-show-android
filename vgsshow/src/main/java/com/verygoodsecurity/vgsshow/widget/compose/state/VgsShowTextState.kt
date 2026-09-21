package com.verygoodsecurity.vgsshow.widget.compose.state

import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.core.VgsShowState

/**
 * State for [com.verygoodsecurity.vgsshow.widget.compose.VgsShowText], produced by
 * [com.verygoodsecurity.vgsshow.widget.compose.rememberVgsShowTextState].
 *
 * The revealed text is intentionally not exposed as public API — matching
 * [com.verygoodsecurity.vgsshow.widget.VGSTextView], which never hands its revealed value to
 * SDK-consumer code either. It can only reach the consumer's app visually (via
 * [com.verygoodsecurity.vgsshow.widget.compose.VgsShowText]) or through the clipboard (via
 * [copyToClipboard]), never by reading a property.
 */
class VgsShowTextState internal constructor(
    contentPath: String,
    internal val text: String,
    // Based on the raw revealed value (VGSTextView.isEmpty()), not `text` — a regex transform
    // can reduce non-empty raw data to an empty string, which must not read as "unrevealed".
    override val isEmpty: Boolean,
    private val onCopy: (VGSTextView.CopyTextFormat, Boolean) -> Unit,
) : VgsShowState(contentPath) {

    /**
     * Copies the revealed text to the clipboard.
     *
     * @param format define in which format text should be copied.
     * @param isSensitive prevents sensitive content from appearing in the visual confirmation
     *   of copied content on Android 13 and higher.
     */
    fun copyToClipboard(
        format: VGSTextView.CopyTextFormat = VGSTextView.CopyTextFormat.RAW,
        isSensitive: Boolean = false,
    ) = onCopy(format, isSensitive)
}
