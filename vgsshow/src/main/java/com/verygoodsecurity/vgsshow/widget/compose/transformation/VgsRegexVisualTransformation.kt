package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState

/**
 * Replaces every match of [regex] in the revealed text with [replacement], e.g. to insert
 * separators into a card number (`4111111111111111` -> `4111 - 1111 - 1111 - 1111`).
 *
 * Mirrors [VGSTextView.addTransformationRegex] — a display-only change. The value held by
 * [VgsTextState] and used for requests is untouched.
 *
 * If applying the replacement fails for any reason, the text is left unchanged rather than
 * throwing, matching the legacy widget's behavior.
 *
 * @param regex Regular expression to match against the revealed text.
 * @param replacement Replacement expression that can include substitutions (e.g. `$1`).
 */
class VgsRegexVisualTransformation(
    private val regex: Regex,
    private val replacement: String,
) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = try {
            regex.replace(text.text, replacement)
        } catch (_: Exception) {
            text.text
        }
        return TransformedText(AnnotatedString(transformed), OffsetMapping.Identity)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsRegexVisualTransformation) return false
        if (regex.pattern != other.regex.pattern) return false
        return replacement == other.replacement
    }

    override fun hashCode(): Int {
        var result = regex.pattern.hashCode()
        result = 31 * result + replacement.hashCode()
        return result
    }
}
