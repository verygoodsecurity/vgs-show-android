package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    init {
        // TODO: read all attributes atc.
    }

    override fun getFieldType(): VGSFieldType {
        TODO("Not yet implemented")
    }

    override fun createChildView() = AppCompatImageView(context)

    override fun saveState(state: Parcelable?): BaseSavedState? = null

    override fun restoreState(state: BaseSavedState) {
        // TODO: restore
    }
}