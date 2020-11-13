package com.verygoodsecurity.vgsshow.widgetnew

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.util.extension.logDebug

class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    init {
        // TODO: read all attributes atc.
    }

    override fun createChildView() = AppCompatImageView(context)

    override fun saveState() {
        // TODO: save
    }

    override fun restoreState() {
        // TODO: restore
    }
}