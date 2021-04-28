package com.verygoodsecurity.vgsshow.pdf

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.github.barteksc.pdfviewer.PDFView
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

class VGSPDFView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<PDFView>(context, attrs, defStyleAttr) {

    override fun getFieldType() = VGSFieldType.PDF

    override fun createChildView() = PDFView(context, null)

    override fun saveState(state: Parcelable?): View.BaseSavedState? = null

    override fun restoreState(state: View.BaseSavedState) {}

    fun share(title: String = "", message: String = "") {
        // TODO: Implement
    }

    @Throws(Exception::class)
    fun export(path: String, name: String) {
        // TODO: Implement
    }

    fun render(bytes: ByteArray) {
        view.fromBytes(bytes).load()
    }

    interface OnLoadingListener

    interface OnPageChangeListener
}