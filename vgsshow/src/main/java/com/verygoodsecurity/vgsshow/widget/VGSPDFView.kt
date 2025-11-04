package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import com.github.barteksc.pdfviewer.PDFView
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import com.verygoodsecurity.vgsshow.widget.extension.getStyledAttributes
import com.verygoodsecurity.vgsshow.widget.view.pdf.state.VGSPDFViewState

/**
 * A `VGSPDFView` is used to display a PDF file.
 *
 * `VGSPDFView` depends on the [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) library.
 * If this dependency is not included, the view will throw an exception.
 * Please add the dependency to your module's `build.gradle` file before using this view:
 *
 * ```gradle
 * implementation 'com.verygoodsecurity:android-pdf-viewer:<latest_version>'
 * ```
 *
 * For more information, please see our [documentation](https://www.verygoodsecurity.com/docs/vgs-show/android-sdk/pdf-view). 
 */
class VGSPDFView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<PDFView>(context, attrs, defStyleAttr) {

    /** The default page to display, indexed from 0. */
    var defaultPage: Int = DEFAULT_PAGE

    /** If `true`, the user can scroll to change pages. */
    var isSwipeEnabled: Boolean = SWIPE_ENABLED

    /** If `true`, horizontal scrolling is enabled. */
    var isSwipeHorizontalEnabled: Boolean = SWIPE_HORIZONTAL_ENABLED

    /** If `true`, double-tapping zooms in on the PDF. */
    var isDoubleTapEnabled: Boolean = DOUBLE_TAB_ENABLED

    /** If `true`, anti-aliasing is used for rendering, which can improve rendering on low-resolution screens. */
    var isAntialiasingEnabled: Boolean = ANTIALIAS_ENABLED

    /** The spacing between pages in dp. */
    var spacing: Int = SPACING

    /** A callback to be invoked when the displayed page changes. */
    var onPageChangeListener: OnPageChangeListener? = null

    /** Returns `true` if a document has been revealed and is ready to be displayed. */
    val hasDocument: Boolean
        get() = documentBytes != null && documentBytes?.isNotEmpty() == true

    internal var onShareDocumentListener: OnShareDocumentListener? = null

    private var documentBytes: ByteArray? = null

    private val renderListeners = mutableListOf<OnRenderStateChangeListener>()

    init {

        context.getStyledAttributes(attrs, R.styleable.VGSPDFView) {
            defaultPage = getInt(R.styleable.VGSPDFView_defaultPage, DEFAULT_PAGE)
            isSwipeEnabled = getBoolean(R.styleable.VGSPDFView_isSwipeEnabled, SWIPE_ENABLED)
            isSwipeHorizontalEnabled = getBoolean(
                R.styleable.VGSPDFView_isSwipeHorizontalEnabled,
                SWIPE_HORIZONTAL_ENABLED
            )
            isDoubleTapEnabled =
                getBoolean(R.styleable.VGSPDFView_isDoubleTapEnabled, DOUBLE_TAB_ENABLED)
            isAntialiasingEnabled =
                getBoolean(R.styleable.VGSPDFView_isAntialiasingEnabled, ANTIALIAS_ENABLED)
            spacing = getInt(R.styleable.VGSPDFView_spacing, 0)
        }
    }

    override fun getFieldType() = VGSFieldType.PDF

    override fun createChildView(
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) = PDFView(context, null)

    override fun saveState(state: Parcelable?) = VGSPDFViewState(state).apply {
        this.documentBytes = this@VGSPDFView.documentBytes
        this.defaultPage = this@VGSPDFView.defaultPage
        this.isSwipeEnabled = this@VGSPDFView.isSwipeEnabled
        this.isSwipeHorizontalEnabled = this@VGSPDFView.isSwipeHorizontalEnabled
        this.isDoubleTapEnabled = this@VGSPDFView.isDoubleTapEnabled
        this.isAntialiasingEnabled = this@VGSPDFView.isAntialiasingEnabled
        this.isAntialiasingEnabled = this@VGSPDFView.isAntialiasingEnabled
        this.spacing = this@VGSPDFView.spacing
    }

    override fun restoreState(state: BaseSavedState) {
        (state as? VGSPDFViewState)?.let {
            this@VGSPDFView.documentBytes = it.documentBytes
            this@VGSPDFView.defaultPage = it.defaultPage
            this@VGSPDFView.isSwipeEnabled = it.isSwipeEnabled
            this@VGSPDFView.isSwipeHorizontalEnabled = it.isSwipeHorizontalEnabled
            this@VGSPDFView.isDoubleTapEnabled = it.isDoubleTapEnabled
            this@VGSPDFView.isAntialiasingEnabled = it.isAntialiasingEnabled
            this@VGSPDFView.isAntialiasingEnabled = it.isAntialiasingEnabled
            this@VGSPDFView.spacing = it.spacing
            refresh()
        }
    }

    /**
     * Registers a callback to be invoked when the document rendering state changes.
     *
     * @param listener @see [OnRenderStateChangeListener].
     */
    fun addRenderingStateChangedListener(listener: OnRenderStateChangeListener): Boolean =
        renderListeners.add(listener)

    /**
     * Unregisters a callback so it will no longer be invoked.
     *
     * @param listener @see [OnRenderStateChangeListener].
     */
    fun removeRenderingStateChangedListener(listener: OnRenderStateChangeListener): Boolean =
        renderListeners.remove(listener)

    /**
     * Call this method when some configuration has been made after rendering PDF content.
     * View will render content according to a new setup.
     */
    fun refresh() {
        documentBytes?.let { render(it) }
    }

    internal fun render(bytes: ByteArray) {
        documentBytes = bytes
        view.fromBytes(bytes)
            .defaultPage(defaultPage)
            .enableSwipe(isSwipeEnabled)
            .swipeHorizontal(isSwipeHorizontalEnabled)
            .enableDoubletap(isDoubleTapEnabled)
            .defaultPage(defaultPage)
            .enableAntialiasing(isAntialiasingEnabled)
            .onLoad { renderListeners.forEach { l -> l.onStart(this, it) } }
            .onRender { pages -> renderListeners.forEach { l -> l.onComplete(this, pages) } }
            .onError { renderListeners.forEach { l -> l.onError(this, it) } }
            .onPageChange { page, count -> onPageChangeListener?.onPageChanged(page, count) }
            .load()
    }

    companion object {

        // Default attributes
        private const val DEFAULT_PAGE = 0
        private const val SWIPE_ENABLED = true
        private const val SWIPE_HORIZONTAL_ENABLED = false
        private const val DOUBLE_TAB_ENABLED = false
        private const val ANTIALIAS_ENABLED = true
        private const val SPACING = 0
    }

    /**
     * Interface definition for a callback to be invoked when document rendering state changed.
     */
    interface OnRenderStateChangeListener {

        /**
         * Called after document is loaded and starts to be rendered.
         *
         * @param view this view.
         * @param pages number of pages.
         */
        fun onStart(view: VGSPDFView, pages: Int)

        /**
         * Called when documents is rendered.
         *
         * @param view this view.
         * @param pages number of pages.
         */
        fun onComplete(view: VGSPDFView, pages: Int)

        /**
         * Called if document is not loaded.
         *
         * @param view this view.
         * @param t reason.
         */
        fun onError(view: VGSPDFView, t: Throwable)
    }

    /**
     * Interface definition for a callback to be invoked when document page changed.
     */
    interface OnPageChangeListener {

        /**
         * Called if document page changed.
         *
         * @param position index of the new selected page.
         * @param pageCount the total page count.
         */
        fun onPageChanged(position: Int, pageCount: Int)
    }

    internal interface OnShareDocumentListener {

        fun onShare(view: VGSPDFView)
    }
}