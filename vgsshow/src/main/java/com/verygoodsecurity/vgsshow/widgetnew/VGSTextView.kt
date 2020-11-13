package com.verygoodsecurity.vgsshow.widgetnew

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class VGSTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatTextView>(context, attrs, defStyleAttr) {

    init {
        // TODO: read all attributes atc.
    }

    override fun createChildView() = AppCompatTextView(context)

    override fun saveState() {
        TODO("Not yet implemented")
    }

    override fun restoreState() {
        TODO("Not yet implemented")
    }
}