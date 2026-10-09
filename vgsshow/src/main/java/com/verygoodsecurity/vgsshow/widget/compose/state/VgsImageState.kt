package com.verygoodsecurity.vgsshow.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.verygoodsecurity.sdk.analytics.utils.Logger
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import com.verygoodsecurity.vgsshow.util.extension.logException
import com.verygoodsecurity.vgsshow.widget.VGSImageView
import com.verygoodsecurity.vgsshow.widget.compose.VgsImage
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

/**
 * Holds the reveal state of a [VgsImage] field.
 *
 * Get an instance from [rememberVgsImageState] — don't construct it yourself. It never exposes
 * the revealed image bytes; the only way to see the image is to render it with [VgsImage].
 */
class VgsImageState internal constructor(
    contentPath: String,
    ignore: Boolean,
    internal val image: ByteArray?,
    internal val bitmap: ImageBitmap? = decodeImage(image),
) : BaseState(contentPath, ignore, VGSFieldType.IMAGE) {

    /** `true` once the image has been revealed for this field. */
    val hasImage: Boolean = image != null

    /** `true` if the revealed image was decoded successfully and is ready to display. */
    val isImageValid: Boolean = bitmap != null

    internal var onLoadCompleteCallback: (() -> Unit)? = null

    internal var onLoadErrorCallback: ((VGSException) -> Unit)? = null

    internal constructor(contentPath: String, ignore: Boolean) : this(contentPath, ignore, null)

    internal fun copy(image: ByteArray?) = VgsImageState(contentPath, ignore, image).withEvents()

    internal fun copy(ignore: Boolean) =
        VgsImageState(contentPath, ignore, image, bitmap).withEvents()

    /** Reports whether the image held by this state decoded, if one has been revealed. */
    internal fun notifyLoadResult() {
        when {
            isImageValid -> onLoadCompleteCallback?.invoke()
            hasImage -> onLoadErrorCallback?.invoke(VGSException.ImageNotValid())
        }
    }

    private fun VgsImageState.withEvents() = apply {
        onLoadCompleteCallback = this@VgsImageState.onLoadCompleteCallback
        onLoadErrorCallback = this@VgsImageState.onLoadErrorCallback
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VgsImageState
        return contentPath == other.contentPath &&
                ignore == other.ignore &&
                image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        var result = contentPath.hashCode()
        result = 31 * result + ignore.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}

private fun decodeImage(image: ByteArray?): ImageBitmap? = try {
    image?.decodeBitmap()?.asImageBitmap()
} catch (e: VGSException.ImageNotValid) {
    Logger.logException(e)
    null
}

/**
 * Clears the revealed image held by this state. Equivalent to [VGSImageView.clear] —
 * [VgsImageState.hasImage] becomes `false` until the field is revealed again.
 *
 * Note: This action can't be reverted.
 */
fun MutableState<VgsImageState>.clearImage() {
    value = value.copy(image = null)
}

/**
 * Creates and remembers image reveal state for use with [VgsImage].
 *
 * Call this once per image field, inside the composable that will display it. [show] takes
 * care of subscribing and unsubscribing this state automatically as the composable enters and
 * leaves the screen — you don't need to call [VGSShow.subscribe] / [VGSShow.unsubscribe]
 * yourself. Once you call [VGSShow.request] or [VGSShow.requestAsync], the returned state
 * updates automatically and [VgsImage] recomposes with the revealed image.
 *
 * @param show The [VGSShow] instance this field belongs to.
 * @param contentPath The path of the value to reveal in the response, e.g. `"json.my_image"`.
 * @param ignore Set to `true` to exclude this field from reveal requests (e.g. to temporarily
 * hide it) without unsubscribing it.
 * @param onLoadComplete Called each time a revealed image has been decoded and is ready to
 * display. Fires whether or not [VgsImage] is currently on screen.
 * @param onLoadError Called each time revealed data could not be decoded as an image.
 * @return State to pass to [VgsImage] via `.value`.
 */
@Composable
fun rememberVgsImageState(
    show: VGSShow,
    contentPath: String,
    ignore: Boolean = false,
    onLoadComplete: (() -> Unit)? = null,
    onLoadError: ((exception: VGSException) -> Unit)? = null,
): MutableState<VgsImageState> {
    val currentOnLoadComplete = rememberUpdatedState(onLoadComplete)
    val currentOnLoadError = rememberUpdatedState(onLoadError)

    val state = remember(contentPath) {
        mutableStateOf(
            VgsImageState(contentPath = contentPath, ignore = ignore).apply {
                onLoadCompleteCallback = { currentOnLoadComplete.value?.invoke() }
                onLoadErrorCallback = { currentOnLoadError.value?.invoke(it) }
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
