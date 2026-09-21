package com.verygoodsecurity.vgsshow.widget.compose

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsShowTextState

/**
 * Fully native (no [androidx.compose.ui.viewinterop.AndroidView]) Compose replacement for
 * [VGSTextView]. Displays the revealed text held by [state].
 *
 * @param state current state, obtained from [rememberVgsShowTextState].
 * @param modifier modifier applied to the underlying [Text].
 * @param color revealed text color.
 * @param style revealed text style.
 * @param visualTransformation applied to the revealed text before it's displayed, e.g.
 *   [com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsShowSecureVisualTransformation]
 *   to mask a range of characters. Does not affect [VgsShowTextState.copyToClipboard].
 */
@Composable
fun VgsShowText(
    state: VgsShowTextState,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val displayedText = remember(state.text, visualTransformation) {
        visualTransformation.filter(AnnotatedString(state.text)).text
    }
    Text(text = displayedText, modifier = modifier, color = color, style = style)
}

/**
 * Creates and remembers a [VgsShowTextState] for [contentPath], subscribing a headless
 * [VGSTextView] to [show] for the lifetime of the call site. The headless view is never
 * attached to the view hierarchy — it only exists to receive revealed values from [show]
 * and hand them back as new [VgsShowTextState] instances.
 *
 * @param show [VGSShow] instance. Pass `null` to leave the state unsubscribed (e.g. previews).
 * @param contentPath text that's used for data transfer between field and VGS proxy. Usually,
 *   it is similar to field-name in JSON path in your inbound route filters.
 * @param transformations regular expression replacements applied to the revealed text, in order,
 *   before it reaches [VgsShowTextState.text]. Re-applied whenever this list changes.
 */
@Composable
fun rememberVgsShowTextState(
    show: VGSShow?,
    contentPath: String,
    transformations: List<Pair<Regex, String>> = emptyList(),
): VgsShowTextState {
    val context = LocalContext.current
    val view = remember(contentPath) {
        VGSTextView(context).apply { setContentPath(contentPath) }
    }

    var state by remember(view) { mutableStateOf(view.toState()) }

    LaunchedEffect(view, transformations) {
        view.clearTransformationRegex()
        transformations.forEach { (regex, replacement) -> view.addTransformationRegex(regex, replacement) }
    }

    DisposableEffect(show, view) {
        view.onValueChangedListener = { _, formattedText -> state = view.toState(formattedText) }
        show?.subscribe(view)
        onDispose {
            view.onValueChangedListener = null
            show?.unsubscribe(view)
        }
    }

    return state
}

private fun VGSTextView.toState(text: String = ""): VgsShowTextState = VgsShowTextState(
    contentPath = getContentPath(),
    text = text,
    isEmpty = isEmpty(),
    onCopy = this::copyToClipboard,
)
