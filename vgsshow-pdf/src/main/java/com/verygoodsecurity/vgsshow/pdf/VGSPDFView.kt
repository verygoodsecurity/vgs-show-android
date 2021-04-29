package com.verygoodsecurity.vgsshow.pdf

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.RestrictTo
import com.github.barteksc.pdfviewer.PDFView
import com.verygoodsecurity.vgsshow.pdf.state.VGSPDFViewState
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 * VGS basic View control that displays reviled PDF documents.
 */
class VGSPDFView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<PDFView>(context, attrs, defStyleAttr) {

    /** Default start page. */
    var defaultPage: Int = 0

    /** Enable/Disable changing pages using scrolling. */
    var isSwipeEnabled: Boolean = true

    /** Enable/Disable horizontal scrolling. */
    var isSwipeHorizontalEnabled: Boolean = false

    /** Enable/Disable double tap. */
    var isDoubleTapEnabled: Boolean = false

    /** Improve rendering on low-res screens. */
    var isAntialiasingEnabled: Boolean = true

    /** Spacing between pages in dp. */
    var spacing: Int = 0

    /** Register a callback to be invoked when documents is loaded successfully. */
    var onLoadCompleteListener: OnLoadCompleteListener? = null

    /** Register a callback to be invoked when documents loading failed. */
    var onErrorListener: OnErrorListener? = null

    private var data: ByteArray? = null

    init {

    }

    /**
     * Gets the current field type.
     *
     * @return [VGSFieldType]
     */
    override fun getFieldType() = VGSFieldType.PDF

    override fun createChildView() = PDFView(context, null)

    override fun saveState(state: Parcelable?) = VGSPDFViewState(null).apply {
        this.data = this@VGSPDFView.data
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
            this@VGSPDFView.data = it.data
            this@VGSPDFView.defaultPage = it.defaultPage
            this@VGSPDFView.isSwipeEnabled = it.isSwipeEnabled
            this@VGSPDFView.isSwipeHorizontalEnabled = it.isSwipeHorizontalEnabled
            this@VGSPDFView.isDoubleTapEnabled = it.isDoubleTapEnabled
            this@VGSPDFView.isAntialiasingEnabled = it.isAntialiasingEnabled
            this@VGSPDFView.isAntialiasingEnabled = it.isAntialiasingEnabled
            this@VGSPDFView.spacing = it.spacing
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    fun render(bytes: ByteArray) {
        view.setOnClickListener { }
        data = bytes
        view.fromBytes(bytes)
            .defaultPage(defaultPage)
            .enableSwipe(isSwipeEnabled)
            .swipeHorizontal(isSwipeHorizontalEnabled)
            .enableDoubletap(isDoubleTapEnabled)
            .defaultPage(defaultPage)
            .enableAntialiasing(isAntialiasingEnabled)
            .onLoad { onLoadCompleteListener?.onLoadComplete(it) }
            .onError { onErrorListener?.onError(it) }
            .load()
    }

    /**
     * Should be called if document already rendered and any changes in setting are made.
     */
    fun refresh() {
        data?.let { render(it) }
    }

    /**
     *
     */
    fun share(title: String = "", message: String = "") {}

    /**
     * Interface definition for a callback to be invoked when documents is loaded successfully.
     */
    interface OnLoadCompleteListener {

        /**
         * Called after document is loaded and starts to be rendered.
         * @param pages number pages that will be rendered.
         */
        fun onLoadComplete(pages: Int)
    }

    /**
     * Interface definition for a callback to be invoked when documents loading failed.
     */
    interface OnErrorListener {


        /**
         * Called if document is not loaded.
         * @param t reason.
         */
        fun onError(t: Throwable)
    }
}