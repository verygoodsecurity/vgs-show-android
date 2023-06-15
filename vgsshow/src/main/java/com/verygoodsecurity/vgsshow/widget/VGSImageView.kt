package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    override fun getFieldType() = VGSFieldType.IMAGE

    override fun createChildView(
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) = AppCompatImageView(context, attrs, defStyleAttr)

    override fun saveState(state: Parcelable?): BaseSavedState? {
        // TODO("Implement")
        return null
    }

    override fun restoreState(state: BaseSavedState) {
        // TODO("Implement")
    }

    internal fun setImageByteArray(image: ByteArray) {
        try {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            view.setImageBitmap(bitmap)
        } catch (e: Exception) {
            logWaring("") // TODO: Add message
        }
    }
}