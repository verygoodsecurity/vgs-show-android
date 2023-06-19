package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    init {

        view.setImageBitmap(null)
    }

    override fun getFieldType() = VGSFieldType.IMAGE

    override fun createChildView(
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) = AppCompatImageView(context, attrs, defStyleAttr)

    override fun saveState(state: Parcelable?): BaseSavedState? = null

    override fun restoreState(state: BaseSavedState) {}

    internal fun setImageByteArray(image: ByteArray) {
        image.decodeBitmap()?.let { view.setImageBitmap(it) } ?: logWaring("") // TODO: Add message
    }
}