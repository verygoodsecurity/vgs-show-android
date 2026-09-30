package com.verygoodsecurity.vgsshow.widget.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsPdfState

/**
 * Securely displays a revealed PDF document.
 *
 * Hosts [VGSPDFView] (and, through it, the AndroidPdfViewer library's `PDFView`) inside an
 * [AndroidView] — PDF rendering is not reimplemented in Compose, since AndroidPdfViewer already
 * does that job well.
 *
 * `VgsPdf` depends on the [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)
 * library, same as [VGSPDFView]. If this dependency is not included, the widget will throw an
 * exception. Please add the dependency to your module's `build.gradle` before using this widget:
 * ```gradle
 * implementation 'com.verygoodsecurity:android-pdf-viewer:<latest_version>'
 * ```
 *
 * Before the document has been revealed — e.g. while waiting for [VGSShow.request] /
 * [VGSShow.requestAsync] to complete — [placeholder] is shown instead, if you provide one.
 *
 * Usage:
 * ```
 * val pdfState = rememberVgsPdfState(show = vgsShow, contentPath = "json.my_pdf", ignore = false)
 * VgsPdf(
 *     state = pdfState.value,
 *     modifier = Modifier.fillMaxSize(),
 *     placeholder = { CircularProgressIndicator() },
 * )
 * ```
 *
 * @param state The current PDF state, from
 * [com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsPdfState].
 * @param modifier The modifier to apply to this layout.
 * @param defaultPage The default page to display, indexed from 0.
 * @param isSwipeEnabled If `true`, the user can scroll to change pages.
 * @param isSwipeHorizontalEnabled If `true`, horizontal scrolling is enabled.
 * @param isDoubleTapEnabled If `true`, double-tapping zooms in on the document.
 * @param isAntialiasingEnabled If `true`, anti-aliasing is used for rendering, which can improve
 * rendering on low-resolution screens.
 * @param spacing The spacing between pages in dp.
 * @param onPageChanged Called when the displayed page changes.
 * @param onRenderComplete Called once the document is rendered, with its page count.
 * @param onRenderError Called if the document fails to render.
 * @param placeholder Optional content shown while there is no document to display yet.
 */
@Composable
fun VgsPdf(
    state: VgsPdfState,
    modifier: Modifier = Modifier,
    defaultPage: Int = 0,
    isSwipeEnabled: Boolean = true,
    isSwipeHorizontalEnabled: Boolean = false,
    isDoubleTapEnabled: Boolean = false,
    isAntialiasingEnabled: Boolean = true,
    spacing: Int = 0,
    onPageChanged: ((position: Int, pageCount: Int) -> Unit)? = null,
    onRenderComplete: ((pages: Int) -> Unit)? = null,
    onRenderError: ((error: Throwable) -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
) {
    val document = state.document
    if (document == null) {
        placeholder?.invoke()
        return
    }

    // Callbacks referenced from the AndroidView `factory` lambda, which runs only once, must be
    // read through `rememberUpdatedState` so the listener created there always sees the latest
    // values instead of the ones captured on first composition.
    val currentState = rememberUpdatedState(state)
    val currentOnPageChanged = rememberUpdatedState(onPageChanged)
    val currentOnRenderComplete = rememberUpdatedState(onRenderComplete)
    val currentOnRenderError = rememberUpdatedState(onRenderError)

    // AndroidPdfViewer has no live setters for these — any change requires a full reload via
    // VGSPDFView.render(). `lastRenderedConfig` tracks what was last applied so `update` (which
    // runs on every recomposition) only reloads when something actually changed. `document` is
    // compared by reference, not content: it only gets a new identity when a value is genuinely
    // (re)revealed, so reference equality is the cheap, correct check here.
    val lastRenderedConfig = remember { arrayOfNulls<List<Any?>>(1) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            VGSPDFView(context).apply {
                addRenderingStateChangedListener(object : VGSPDFView.OnRenderStateChangeListener {

                    override fun onStart(view: VGSPDFView, pages: Int) {}

                    override fun onComplete(view: VGSPDFView, pages: Int) {
                        currentState.value.onRenderCompleteEvent?.invoke()
                        currentOnRenderComplete.value?.invoke(pages)
                    }

                    override fun onError(view: VGSPDFView, t: Throwable) {
                        currentState.value.onRenderErrorEvent?.invoke()
                        currentOnRenderError.value?.invoke(t)
                    }
                })
                onPageChangeListener = object : VGSPDFView.OnPageChangeListener {
                    override fun onPageChanged(position: Int, pageCount: Int) {
                        currentOnPageChanged.value?.invoke(position, pageCount)
                    }
                }
            }
        },
        update = { pdfView ->
            pdfView.defaultPage = defaultPage
            pdfView.isSwipeEnabled = isSwipeEnabled
            pdfView.isSwipeHorizontalEnabled = isSwipeHorizontalEnabled
            pdfView.isDoubleTapEnabled = isDoubleTapEnabled
            pdfView.isAntialiasingEnabled = isAntialiasingEnabled
            pdfView.spacing = spacing

            val config = listOf(
                document,
                defaultPage,
                isSwipeEnabled,
                isSwipeHorizontalEnabled,
                isDoubleTapEnabled,
                isAntialiasingEnabled,
                spacing,
            )
            if (lastRenderedConfig[0] != config) {
                lastRenderedConfig[0] = config
                pdfView.render(document)
            }
        },
    )
}
