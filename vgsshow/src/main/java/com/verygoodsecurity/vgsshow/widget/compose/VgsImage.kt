package com.verygoodsecurity.vgsshow.widget.compose

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsImageState

/**
 * Securely displays a revealed image.
 *
 * Before the image has been revealed — e.g. while waiting for [VGSShow.request] /
 * [VGSShow.requestAsync] to complete, or if decoding fails — [placeholder] is shown instead, if
 * you provide one. Check [state]'s [VgsImageState.hasImage] / [VgsImageState.isImageValid] if you
 * want to render your own loading or error UI instead of a static placeholder.
 *
 * Usage:
 * ```
 * val imageState = rememberVgsImageState(show = vgsShow, contentPath = "json.my_image", ignore = false)
 * VgsImage(
 *     state = imageState.value,
 *     contentDescription = "ID scan",
 *     placeholder = { CircularProgressIndicator() },
 * )
 * ```
 *
 * @param state The current image state, from [com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsImageState].
 * @param contentDescription Accessibility description for the image, or `null` if none.
 * @param modifier The modifier to apply to this layout.
 * @param alignment Alignment of the image within its bounds.
 * @param contentScale How the image is scaled to fit its bounds.
 * @param alpha The opacity to apply when drawing the image.
 * @param colorFilter An optional color filter to apply to the image.
 * @param placeholder Optional content shown while there is no image to display yet.
 */
@Composable
fun VgsImage(
    state: VgsImageState,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    placeholder: (@Composable () -> Unit)? = null
) {
    state.bitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter
        )
    } ?: placeholder?.invoke()
}
