package com.verygoodsecurity.vgsshow.widget.textview.state

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.View

internal class TextViewState : View.BaseSavedState {

    var text: CharSequence? = null

    var defaultText: CharSequence? = null

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<TextViewState> {
            override fun createFromParcel(source: Parcel): TextViewState {
                return TextViewState(source)
            }

            override fun newArray(size: Int): Array<TextViewState?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(superState: Parcelable?) : super(superState)

    constructor(`in`: Parcel) : super(`in`) {
        text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        defaultText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        TextUtils.writeToParcel(text, out, flags)
        TextUtils.writeToParcel(defaultText, out, flags)
    }
}