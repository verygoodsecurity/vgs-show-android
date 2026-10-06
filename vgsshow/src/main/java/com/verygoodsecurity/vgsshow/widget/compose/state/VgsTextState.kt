package com.verygoodsecurity.vgsshow.widget.compose.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.AnnotatedString
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.compose.VgsText
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsChainedVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsSecureVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsVisualTransformation
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.extension.copyToClipboard
import com.verygoodsecurity.vgsshow.widget.VGSTextView

private const val EMPTY = ""

private fun VgsVisualTransformation.containsSecureRange(): Boolean = when (this) {
    is VgsSecureVisualTransformation -> true
    is VgsChainedVisualTransformation -> transformations.any { it.containsSecureRange() }
    else -> false
}

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
    internal val displayText: AnnotatedString =
        visualTransformation.filter(AnnotatedString(rawText)).text

    /** `true` if this field's visual transformation masks a secure range. */
    internal val isSecure: Boolean = visualTransformation.containsSecureRange()

    internal var copyToClipboardEvent: ((format: VGSAnalyticsCopyFormat) -> Unit)? = null

    val isEmpty: Boolean
        get() = rawText.isBlank()

    fun copyRawToClipboard(
        context: Context,
        isSensitive: Boolean = false
    ) {
        if (isEmpty) return
        context.copyToClipboard(rawText, isSensitive)
        copyToClipboardEvent?.invoke(VGSAnalyticsCopyFormat.RAW)
    }

    fun copyFormattedToClipboard(
        context: Context,
        isSensitive: Boolean = false
    ) {
        val text = displayText.text
        if (isEmpty) return
        context.copyToClipboard(text, isSensitive)
        copyToClipboardEvent?.invoke(VGSAnalyticsCopyFormat.FORMATTED)
    }

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
    ).also { it.copyToClipboardEvent = copyToClipboardEvent }

    internal fun copy(ignore: Boolean, visualTransformation: VgsVisualTransformation) = VgsTextState(
        contentPath = contentPath,
        ignore = ignore,
        rawText = rawText,
        visualTransformation = visualTransformation,
    ).also { it.copyToClipboardEvent = copyToClipboardEvent }

    internal fun hasConfig(ignore: Boolean, visualTransformation: VgsVisualTransformation): Boolean =
        this.ignore == ignore && this.visualTransformation == visualTransformation

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VgsTextState
        return contentPath == other.contentPath &&
                ignore == other.ignore &&
                rawText == other.rawText &&
                visualTransformation == other.visualTransformation
    }

    override fun hashCode(): Int {
        var result = contentPath.hashCode()
        result = 31 * result + ignore.hashCode()
        result = 31 * result + rawText.hashCode()
        result = 31 * result + visualTransformation.hashCode()
        return result
    }
}

/**
 * Clears the revealed text held by this state. Equivalent to [VGSTextView.clearText] —
 * [VgsTextState.isEmpty] becomes `true` until the field is revealed again.
 *
 * Note: This action can't be reverted.
 */
fun MutableState<VgsTextState>.clearText() {
    value = value.copy(text = EMPTY)
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

    SideEffect {
        val current = state.value
        if (!current.hasConfig(ignore, visualTransformation)) {
            state.value = current.copy(ignore = ignore, visualTransformation = visualTransformation)
        }
    }

    DisposableEffect(show, contentPath) {
        show.subscribe(state)
        onDispose {
            show.unsubscribe(state)
        }
    }

    return state
}
