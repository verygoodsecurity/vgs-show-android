package com.verygoodsecurity.vgsshow.widget.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSImageView
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 *  [VGSTextView] compose wrapper.
 *
 *  @param show - [VGSShow] instance.
 *  @param contentPath - text that's used for data transfer between field and VGS proxy. Usually, it is similar to field-name in JSON path in your inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextView] is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun VGSTextViewWrapper(
    show: VGSShow?,
    contentPath: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextView) -> Unit)? = null,
    onViewUpdate: ((VGSTextView) -> Unit)? = null,
) {
    Root(
        show = show,
        contentPath = contentPath,
        createView = { VGSTextView(it) },
        modifier = modifier,
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [VGSImageView] compose wrapper.
 *
 *  @param show - [VGSShow] instance.
 *  @param contentPath - text that's used for data transfer between field and VGS proxy. Usually, it is similar to field-name in JSON path in your inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSImageView] is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun VGSImageViewWrapper(
    show: VGSShow?,
    contentPath: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSImageView) -> Unit)? = null,
    onViewUpdate: ((VGSImageView) -> Unit)? = null,
) {
    Root(
        show = show,
        contentPath = contentPath,
        createView = { VGSImageView(it) },
        modifier = modifier,
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [VGSPDFView] compose wrapper.
 *
 *  @param show - [VGSShow] instance.
 *  @param contentPath - text that's used for data transfer between field and VGS proxy. Usually, it is similar to field-name in JSON path in your inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSPDFView] is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun VGSPDFViewWrapper(
    show: VGSShow?,
    contentPath: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSPDFView) -> Unit)? = null,
    onViewUpdate: ((VGSPDFView) -> Unit)? = null,
) {
    Root(
        show = show,
        contentPath = contentPath,
        createView = { VGSPDFView(it) },
        modifier = modifier,
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

@Composable
internal fun <T : VGSView<*>> Root(
    show: VGSShow?,
    contentPath: String,
    modifier: Modifier = Modifier,
    createView: (Context) -> T,
    onViewCreate: ((T) -> Unit)?,
    onViewUpdate: ((T) -> Unit)?,
) {
    var input by remember { mutableStateOf<T?>(null) }

    DisposableEffect(Unit) {

        onDispose {
            input?.let { show?.unsubscribe(it) }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            val view = createView(it).also { view -> input = view }
            view.setContentPath(contentPath)
            onViewCreate?.invoke(view)
            show?.subscribe(view)
            view
        },
        update = { view -> onViewUpdate?.invoke(view) }
    )
}