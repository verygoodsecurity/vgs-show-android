package com.verygoodsecurity.vgsshow.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.compose.VgsPdf
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

/**
 * Holds the reveal state of a [VgsPdf] field.
 *
 * Get an instance from [rememberVgsPdfState] — don't construct it yourself. It never exposes
 * the revealed PDF bytes; the only way to display the document is to render it with [VgsPdf].
 */
class VgsPdfState internal constructor(
    contentPath: String,
    ignore: Boolean,
    internal val document: ByteArray?,
) : BaseState(contentPath, ignore, VGSFieldType.PDF) {

    /** `true` once a document has been revealed for this field. */
    val hasDocument: Boolean = document != null

    internal var onRenderCompleteEvent: (() -> Unit)? = null

    internal var onRenderErrorEvent: (() -> Unit)? = null

    internal var onRenderStartCallback: ((pages: Int) -> Unit)? = null

    internal var onRenderCompleteCallback: ((pages: Int) -> Unit)? = null

    internal var onRenderErrorCallback: ((error: Throwable) -> Unit)? = null

    internal var onPageChangedCallback: ((position: Int, pageCount: Int) -> Unit)? = null

    internal constructor(contentPath: String, ignore: Boolean) : this(contentPath, ignore, null)

    internal fun copy(document: ByteArray?) =
        VgsPdfState(contentPath, ignore, document).withEvents()

    internal fun copy(ignore: Boolean) = VgsPdfState(contentPath, ignore, document).withEvents()

    private fun VgsPdfState.withEvents() = apply {
        onRenderCompleteEvent = this@VgsPdfState.onRenderCompleteEvent
        onRenderErrorEvent = this@VgsPdfState.onRenderErrorEvent
        onRenderStartCallback = this@VgsPdfState.onRenderStartCallback
        onRenderCompleteCallback = this@VgsPdfState.onRenderCompleteCallback
        onRenderErrorCallback = this@VgsPdfState.onRenderErrorCallback
        onPageChangedCallback = this@VgsPdfState.onPageChangedCallback
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VgsPdfState
        return contentPath == other.contentPath &&
                ignore == other.ignore &&
                document.contentEquals(other.document)
    }

    override fun hashCode(): Int {
        var result = contentPath.hashCode()
        result = 31 * result + ignore.hashCode()
        result = 31 * result + document.contentHashCode()
        return result
    }
}

/**
 * Clears the revealed document held by this state. [VgsPdfState.hasDocument] becomes `false`
 * until the field is revealed again.
 *
 * Note: This action can't be reverted.
 */
fun MutableState<VgsPdfState>.clearDocument() {
    value = value.copy(document = null)
}

/**
 * Creates and remembers PDF reveal state for use with [VgsPdf].
 *
 * Call this once per PDF field, inside the composable that will display it. [show] takes care
 * of subscribing and unsubscribing this state automatically as the composable enters and leaves
 * the screen — you don't need to call [VGSShow.subscribe] / [VGSShow.unsubscribe] yourself. Once
 * you call [VGSShow.request] or [VGSShow.requestAsync], the returned state updates automatically
 * and [VgsPdf] recomposes with the revealed document.
 *
 * @param show The [VGSShow] instance this field belongs to.
 * @param contentPath The path of the value to reveal in the response, e.g. `"json.my_pdf"`.
 * @param ignore Set to `true` to exclude this field from reveal requests (e.g. to temporarily
 * hide it) without unsubscribing it.
 * @param onRenderStart Called when the document has loaded and rendering begins, with its page
 * count. Only fires while [VgsPdf] is in composition, since rendering happens inside it.
 * @param onRenderComplete Called once the document is rendered, with its page count.
 * @param onRenderError Called if the document fails to render.
 * @param onPageChanged Called when the displayed page changes, with the new position (indexed
 * from 0) and the page count.
 * @return State to pass to [VgsPdf] via `.value`.
 */
@Composable
fun rememberVgsPdfState(
    show: VGSShow,
    contentPath: String,
    ignore: Boolean = false,
    onRenderStart: ((pages: Int) -> Unit)? = null,
    onRenderComplete: ((pages: Int) -> Unit)? = null,
    onRenderError: ((error: Throwable) -> Unit)? = null,
    onPageChanged: ((position: Int, pageCount: Int) -> Unit)? = null,
): MutableState<VgsPdfState> {
    val currentOnRenderStart = rememberUpdatedState(onRenderStart)
    val currentOnRenderComplete = rememberUpdatedState(onRenderComplete)
    val currentOnRenderError = rememberUpdatedState(onRenderError)
    val currentOnPageChanged = rememberUpdatedState(onPageChanged)

    val state = remember(contentPath) {
        mutableStateOf(
            VgsPdfState(contentPath = contentPath, ignore = ignore).apply {
                onRenderStartCallback = { currentOnRenderStart.value?.invoke(it) }
                onRenderCompleteCallback = { currentOnRenderComplete.value?.invoke(it) }
                onRenderErrorCallback = { currentOnRenderError.value?.invoke(it) }
                onPageChangedCallback = { position, pageCount ->
                    currentOnPageChanged.value?.invoke(position, pageCount)
                }
            }
        )
    }

    if (state.value.ignore != ignore) {
        state.value = state.value.copy(ignore = ignore)
    }

    DisposableEffect(show, contentPath) {
        show.subscribe(state)
        onDispose {
            show.unsubscribe(state)
        }
    }

    return state
}
