package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.compose.VgsText
import com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsTextState

/**
 * Type accepted by [rememberVgsTextState]'s `visualTransformation` parameter, used to change how
 * the value [VgsText] renders without altering the underlying revealed value itself. Built-in
 * options: [VgsRegexVisualTransformation] (reformat the revealed text, e.g. insert separators),
 * [VgsSecureVisualTransformation] (mask a range of the revealed text, e.g. `4111••••••••1111`),
 * [VgsChainedVisualTransformation] (combine several transformations, e.g. format then mask), and
 * [None] (no change).
 */
abstract class VgsVisualTransformation internal constructor() {

    /** Returns [text] with this transformation applied, ready to render. */
    abstract fun filter(text: AnnotatedString): AnnotatedString

    companion object {

        /** No visual transformation; the value is displayed as revealed. */
        val None: VgsVisualTransformation = object : VgsVisualTransformation() {

            override fun filter(text: AnnotatedString): AnnotatedString = text
        }
    }
}
