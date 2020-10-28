package com.verygoodsecurity.vgsshow.core

import android.util.Log
import com.verygoodsecurity.vgsshow.widget.VGSTextView

class VGSShow {

    private val store = mutableListOf<VGSTextView>()

    fun bindView(v: VGSTextView) {
        store.add(v)
    }

    fun unbind(v: VGSTextView) {
        store.remove(v)
    }

    fun request(fieldName: String, token: String) {
        store.forEach {
            if (fieldName == it.getFieldName()) {
                it.setText("fn: $fieldName, token: $token")
                Log.e("test", "Success! fn: $fieldName, token: $token")
            } else {
                Log.e("test", "Failed! fn: $fieldName, token: $token")
            }
        }
    }
}