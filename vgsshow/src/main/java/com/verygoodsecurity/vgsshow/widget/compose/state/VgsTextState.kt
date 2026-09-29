package com.verygoodsecurity.vgsshow.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.AnnotatedString
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.compose.VgsText
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsVisualTransformation
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

private const val EMPTY = ""

/**
 * Holds the reveal state of a [VgsText] field.
 *
 * Get an instance from [rememberVgsTextState] — don't construct it yourself. It never exposes
 * the raw revealed text; the only way to see it is [displayText], which already has
 * [visualTransformation] applied.
 */
class VgsTextState internal constructor(
    contentPath: String,
    ignore: Boolean,
    internal val rawText: String,
    private val visualTransformation: VgsVisualTransformation,
) : BaseState(contentPath, ignore, VGSFieldType.INFO) {

    /** The revealed text with this field's visual transformation applied, ready to render. */
    internal val displayText: AnnotatedString = visualTransformation.filter(AnnotatedString(rawText)).text

    val isEmpty: Boolean
        get() = rawText.isBlank()

    internal constructor(
        contentPath: String,
        ignore: Boolean,
        visualTransformation: VgsVisualTransformation,
    ) : this(contentPath, ignore, EMPTY, visualTransformation)

    internal fun copy(text: String) = VgsTextState(
        contentPath = contentPath,
        ignore = ignore,
        rawText = text,
        visualTransformation = visualTransformation,
    )
}

/**
 * Creates and remembers text reveal state for use with [VgsText].
 *
 * Call this once per text field, inside the composable that will display it. [show] takes care
 * of subscribing and unsubscribing this state automatically as the composable enters and leaves
 * the screen — you don't need to call [VGSShow.subscribe] / [VGSShow.unsubscribe] yourself. Once
 * you call [VGSShow.request] or [VGSShow.requestAsync], the returned state updates automatically
 * and [VgsText] recomposes with the revealed text.
 *
 * @param show The [VGSShow] instance this field belongs to.
 * @param contentPath The path of the value to reveal in the response, e.g. `"json.my_data"`.
 * @param ignore Set to `true` to exclude this field from reveal requests (e.g. to temporarily
 * hide it) without unsubscribing it.
 * @param visualTransformation Optional [VgsVisualTransformation] applied to the revealed text,
 * e.g. to mask part of it. Applied once per revealed value, not on every recomposition.
 * @return State to pass to [VgsText] via `.value`.
 */
@Composable
fun rememberVgsTextState(
    show: VGSShow,
    contentPath: String,
    ignore: Boolean,
    visualTransformation: VgsVisualTransformation = VgsVisualTransformation.None,
): MutableState<VgsTextState> {
    fun newState() = VgsTextState(contentPath, ignore, visualTransformation)

    val state = rememberSaveable(
        contentPath,
        saver = Saver(
            save = { it.value.rawText },
            restore = { savedValue -> mutableStateOf(newState().copy(text = savedValue)) },
        ),
    ) {
        mutableStateOf(newState())
    }

    DisposableEffect(show, contentPath) {
        show.subscribe(state)
        onDispose {
            show.unsubscribe(state)
        }
    }

    return state
}
