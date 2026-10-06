package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString

/**
 * Visual transformation that applies multiple [VgsVisualTransformation]s in sequence.
 *
 * Each transformation receives the output of the previous one. For example, chain
 * [VgsRegexVisualTransformation] followed by [VgsSecureVisualTransformation] to format a revealed
 * card number and then mask part of the formatted result.
 *
 * @param transformations ordered list of transformations to apply. Order matters — the first
 * transformation is applied first, and each subsequent one transforms the result of the previous.
 */
class VgsChainedVisualTransformation(val transformations: List<VgsVisualTransformation>) :
    VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): AnnotatedString {
        return transformations.fold(text) { value, transformation -> transformation.filter(value) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsChainedVisualTransformation) return false
        return transformations == other.transformations
    }

    override fun hashCode(): Int {
        return transformations.hashCode()
    }
}
