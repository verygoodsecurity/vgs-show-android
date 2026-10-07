package com.verygoodsecurity.vgsshow.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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

    internal constructor(contentPath: String, ignore: Boolean) : this(contentPath, ignore, null)

    internal fun copy(document: ByteArray?) = VgsPdfState(contentPath, ignore, document).also {
        it.onRenderCompleteEvent = onRenderCompleteEvent
        it.onRenderErrorEvent = onRenderErrorEvent
    }

    internal fun copy(ignore: Boolean) = VgsPdfState(contentPath, ignore, document).also {
        it.onRenderCompleteEvent = onRenderCompleteEvent
        it.onRenderErrorEvent = onRenderErrorEvent
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
 * @return State to pass to [VgsPdf] via `.value`.
 */
@Composable
fun rememberVgsPdfState(
    show: VGSShow,
    contentPath: String,
    ignore: Boolean = false,
): MutableState<VgsPdfState> {
    val state = rememberSaveable(
        contentPath,
        saver = Saver(
            save = { it.value.document },
            restore = { savedDocument ->
                mutableStateOf(
                    VgsPdfState(
                        contentPath = contentPath,
                        ignore = ignore,
                        document = savedDocument
                    )
                )
            },
        ),
    ) {
        mutableStateOf(VgsPdfState(contentPath = contentPath, ignore = ignore))
    }

    SideEffect {
        val current = state.value
        if (current.ignore != ignore) {
            state.value = current.copy(ignore = ignore)
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
