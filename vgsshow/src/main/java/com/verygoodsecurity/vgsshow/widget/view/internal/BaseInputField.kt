package com.verygoodsecurity.vgsshow.widget.view.internal

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatTextView

class BaseInputField(context: Context) : AppCompatTextView(context) {

    var fieldName: String? = null

    fun getSaveState(state: Parcelable?): BaseSavedState {
        return with(InnerState(state)) {
            this.text = this@BaseInputField.text.toString()
            this
        }
    }

    fun setRestoreState(state: Parcelable?) {
        if (state is InnerState) {
            setText(state.text)
        }
    }

    protected class InnerState : BaseSavedState {
        var text: CharSequence? = null

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<InnerState> {
                override fun createFromParcel(source: Parcel): InnerState {
                    return InnerState(source)
                }

                override fun newArray(size: Int): Array<InnerState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
        }
    }


    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(false)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {}

}