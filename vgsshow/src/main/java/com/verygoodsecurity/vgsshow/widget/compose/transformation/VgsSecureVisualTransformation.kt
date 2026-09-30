package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange

/**
 * Masks [ranges] of the text displayed by [com.verygoodsecurity.vgsshow.widget.compose.VgsText]
 * with [secureSymbol] (e.g. `4111••••••••1111`).
 *
 * Delegates to [SecureTransformationMethod] — the same masking algorithm the legacy
 * [VGSTextView.isSecureText] feature uses — so both widgets mask identically and the logic
 * only lives in one place.
 *
 * @param ranges ranges of the text that should be masked.
 * @param secureSymbol character that replaces every masked character. Defaults to
 *   [VGSTextView.SECURE_SYMBOL], matching the legacy widget's default.
 */
class VgsSecureVisualTransformation(
    private val ranges: Array<VGSTextRange>,
    private val secureSymbol: Char = VGSTextView.SECURE_SYMBOL,
) : VgsVisualTransformation() {

    constructor(
        range: VGSTextRange,
        secureSymbol: Char = VGSTextView.SECURE_SYMBOL,
    ) : this(arrayOf(range), secureSymbol)

    private val method = SecureTransformationMethod(secureSymbol, ranges)

    override fun filter(text: AnnotatedString): TransformedText {
        val masked = method.getTransformation(text.text, null).toString()
        return TransformedText(AnnotatedString(masked, text.spanStyles), OffsetMapping.Identity)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsSecureVisualTransformation) return false
        if (secureSymbol != other.secureSymbol) return false
        return ranges.contentEquals(other.ranges)
    }

    override fun hashCode(): Int {
        var result = secureSymbol.hashCode()
        result = 31 * result + ranges.contentHashCode()
        return result
    }
}
