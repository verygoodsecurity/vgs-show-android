package com.verygoodsecurity.vgsshow.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
    internal val image: ByteArray?
) : BaseState(contentPath, ignore, VGSFieldType.IMAGE) {

    internal val bitmap: ImageBitmap? = try {
        image?.decodeBitmap()?.asImageBitmap()
    } catch (e: VGSException.ImageNotValid) {
        logException(e)
        null
    }

    /** `true` once the image has been revealed for this field. */
    val hasImage: Boolean = image != null

    /** `true` if the revealed image was decoded successfully and is ready to display. */
    val isImageValid: Boolean = bitmap != null

    internal constructor(contentPath: String, ignore: Boolean) : this(contentPath, ignore, null)

    internal fun copy(image: ByteArray?) = VgsImageState(contentPath, ignore, image)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VgsImageState
        return contentPath == other.contentPath && image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        return 31 * contentPath.hashCode() + image.contentHashCode()
    }
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
 * @return State to pass to [VgsImage] via `.value`.
 */
@Composable
fun rememberVgsImageState(
    show: VGSShow,
    contentPath: String,
    ignore: Boolean,
): MutableState<VgsImageState> {
    val state = rememberSaveable(
        contentPath,
        saver = Saver(
            save = { it.value.image },
            restore = { savedImage ->
                mutableStateOf(
                    VgsImageState(
                        contentPath = contentPath,
                        ignore = ignore,
                        image = savedImage
                    )
                )
            },
        ),
    ) {
        mutableStateOf(VgsImageState(contentPath = contentPath, ignore = ignore))
    }

    DisposableEffect(show, contentPath) {
        show.subscribe(state)
        onDispose {
            show.unsubscribe(state)
        }
    }

    return state
}
