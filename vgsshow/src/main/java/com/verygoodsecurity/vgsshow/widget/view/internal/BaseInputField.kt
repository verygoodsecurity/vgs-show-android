package com.verygoodsecurity.vgsshow.widget.view.internal

import android.content.Context
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatTextView

class BaseInputField(context: Context) : AppCompatTextView(context) {

    var fieldName: String? = null

    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(false)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {}
}