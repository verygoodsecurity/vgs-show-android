package com.verygoodsecurity.vgsshow.widget.compose

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsImageState

/**
 * Securely displays a revealed image.
 *
 * Draws nothing until an image has been revealed and decoded. To show your own loading or error
 * UI, branch on [state] — [VgsImageState.hasImage] is `true` once data has arrived, and
 * [VgsImageState.isImageValid] is `true` only if it decoded successfully:
 * ```
 * val imageState = rememberVgsImageState(show = vgsShow, contentPath = "json.my_image")
 * if (imageState.value.isImageValid) {
 *     VgsImage(state = imageState.value, contentDescription = "ID scan")
 * } else {
 *     CircularProgressIndicator()
 * }
 * ```
 * Use [onLoadComplete] / [onLoadError] for side effects such as analytics.
 *
 * @param state The current image state, from [com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsImageState].
 * @param contentDescription Accessibility description for the image, or `null` if none.
 * @param modifier The modifier to apply to this layout.
 * @param alignment Alignment of the image within its bounds.
 * @param contentScale How the image is scaled to fit its bounds.
 * @param alpha The opacity to apply when drawing the image.
 * @param colorFilter An optional color filter to apply to the image.
 * @param onLoadComplete Called when a revealed image has been decoded and is ready to display.
 * @param onLoadError Called when revealed data could not be decoded as an image.
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
    onLoadComplete: (() -> Unit)? = null,
    onLoadError: ((exception: VGSException) -> Unit)? = null,
) {
    val currentOnLoadComplete = rememberUpdatedState(onLoadComplete)
    val currentOnLoadError = rememberUpdatedState(onLoadError)

    // Decoding happens when the state is created, so report its outcome once per state change.
    LaunchedEffect(state) {
        when {
            state.isImageValid -> currentOnLoadComplete.value?.invoke()
            state.hasImage -> currentOnLoadError.value?.invoke(VGSException.ImageNotValid())
        }
    }

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
    }
}
