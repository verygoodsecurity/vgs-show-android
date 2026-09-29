package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.VgsText

/**
 * Compose [VisualTransformation] type accepted by [VgsText].
 *
 * Pass one of the built-in implementations to [VgsText]'s `visualTransformation` parameter to
 * change how the revealed value is displayed, without altering the underlying revealed value
 * itself. Built-in options: [VgsSecureVisualTransformation] (mask a range of the revealed
 * text, e.g. `4111••••••••1111`) and [None] (no change).
 */
abstract class VgsVisualTransformation internal constructor() : VisualTransformation {

    companion object {

        /** No visual transformation; the value is displayed as revealed. */
        val None: VgsVisualTransformation = object : VgsVisualTransformation() {

            override fun filter(text: AnnotatedString): TransformedText {
                return TransformedText(text, OffsetMapping.Identity)
            }
        }
    }
}
