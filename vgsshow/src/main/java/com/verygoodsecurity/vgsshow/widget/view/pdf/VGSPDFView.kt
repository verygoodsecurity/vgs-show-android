package com.verygoodsecurity.vgsshow.widget.view.pdf

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.content.FileProvider
import com.github.barteksc.pdfviewer.PDFView
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.util.extension.toFile
import com.verygoodsecurity.vgsshow.util.extension.toShareIntent
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import com.verygoodsecurity.vgsshow.widget.extension.getStyledAttributes
import com.verygoodsecurity.vgsshow.widget.view.pdf.state.VGSPDFViewState
import java.io.File

/**
 * A VGSPDFView can be used to display a PDF file.
 *
 * VGSPDFView depends on [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) library.
 * When View is initialized without additional dependency, the View will throw Exception.
 * Please add the dependency to your module gradle file before using this View:
 *
 * implementation 'com.github.barteksc:android-pdf-viewer:2.8.2'
 *
 * For more information please visit our [guide](). TODO: add guide link
 */
class VGSPDFView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<PDFView>(context, attrs, defStyleAttr) {

    /** Default start page. */
    var defaultPage: Int = DEFAULT_PAGE

    /** Enable/Disable changing pages using scrolling. */
    var isSwipeEnabled: Boolean = SWIPE_ENABLED

    /** Enable/Disable horizontal scrolling. */
    var isSwipeHorizontalEnabled: Boolean = SWIPE_HORIZONTAL_ENABLED

    /** Enable/Disable double tap. */
    var isDoubleTapEnabled: Boolean = DOUBLE_TAB_ENABLED

    /** Improve rendering on low-res screens. */
    var isAntialiasingEnabled: Boolean = ANTIALIAS_ENABLED

    /** Spacing between pages in dp. */
    var spacing: Int = SPACING

    /** Register a callback to be invoked when document rendering state changed. */
    var onRenderStateChangeListener: OnRenderStateChangeListener? = null

    /** Register a callback to be invoked when document page changed. */
    var onPageChangeListener: OnPageChangeListener? = null

    /** Return true if document was revealed. */
    var hasDocument: Boolean = false
        private set

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

    override fun createChildView() = PDFView(context, null)

    override fun saveState(state: Parcelable?) = VGSPDFViewState(state).apply {
        this.hasDocument = this@VGSPDFView.hasDocument
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
            this@VGSPDFView.hasDocument = it.hasDocument
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
     * Call this method when some configuration has been made after rendering PDF content.
     * View will render content according to a new setup.
     */
    fun refresh() {
        if (hasDocument) {
            getDocumentFile()?.let { render(it) }
        }
    }

    /**
     * Share PDF file using system app chooser dialog.
     *
     * @param chooserTitle define system chooser dialog title.
     * @param chooserMessage define system chooser dialog message.
     */
    fun sharePDF(chooserTitle: String = PDF_FILE_NAME, chooserMessage: String = "") {
        getDocumentFile()?.let { file ->
            val documentUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
            val intent = documentUri.toShareIntent(chooserTitle, chooserMessage, PDF_MIME_TYPE)
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }

    internal fun render(bytes: ByteArray) {
        bytes.toFile(getVGSFilesDirectory(), PDF_FILE_NAME)?.let {
            this.hasDocument = true
            render(it)
        }
    }

    private fun render(file: File) {
        view.fromFile(file)
            .defaultPage(defaultPage)
            .enableSwipe(isSwipeEnabled)
            .swipeHorizontal(isSwipeHorizontalEnabled)
            .enableDoubletap(isDoubleTapEnabled)
            .defaultPage(defaultPage)
            .enableAntialiasing(isAntialiasingEnabled)
            .onLoad { onRenderStateChangeListener?.onStart(it) }
            .onRender { pages, _, _ -> onRenderStateChangeListener?.onComplete(pages) }
            .onError { onRenderStateChangeListener?.onError(it) }
            .onPageChange { page, count -> onPageChangeListener?.onPageChanged(page, count) }
            .load()
    }

    private fun getDocumentFile(): File? {
        val file = File(getVGSFilesDirectory(), PDF_FILE_NAME)
        return if (file.exists()) file else null
    }

    private fun getVGSFilesDirectory(): File {
        return File(context.filesDir, VGS_FILES_DIRECTORY).also {
            if (!it.exists()) {
                it.mkdir()
            }
        }
    }

    companion object {

        // Default attributes
        private const val DEFAULT_PAGE = 0
        private const val SWIPE_ENABLED = true
        private const val SWIPE_HORIZONTAL_ENABLED = false
        private const val DOUBLE_TAB_ENABLED = false
        private const val ANTIALIAS_ENABLED = true
        private const val SPACING = 0

        // File constants
        private const val VGS_FILES_DIRECTORY = "vgs"
        private const val PDF_FILE_NAME = "document.pdf"
        private const val PDF_MIME_TYPE = "application/pdf"
        private const val FILE_PROVIDER_AUTHORITY = "com.verygoodsecurity.vgsshow.provider"
    }

    /**
     * Interface definition for a callback to be invoked when document rendering state changed.
     */
    interface OnRenderStateChangeListener {

        /**
         * Called after document is loaded and starts to be rendered.
         *
         * @param pages number of pages.
         */
        fun onStart(pages: Int)

        /**
         * Called when documents is rendered.
         *
         * @param pages number of pages.
         */
        fun onComplete(pages: Int)

        /**
         * Called if document is not loaded.
         *
         * @param t reason.
         */
        fun onError(t: Throwable)
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
}