package com.verygoodsecurity.vgsshow.widget.view.internal

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatTextView
import com.verygoodsecurity.vgsshow.widget.VGSTextView

class BaseInputField(context: Context) : AppCompatTextView(context) {

    var defaultText: CharSequence? = ""
        set(value) {
            field = value
            text = value
        }
    var fieldName: String? = null

    private var textChangeListener: VGSTextView.OnTextChangedListener? = null

    init {
        setEditPermission(true)
        addTextChangedListener(InnerTextWatcher())
        setEditPermission(false)
    }


    fun getSaveState(state: Parcelable?): BaseSavedState {
        return with(InnerState(state)) {
            this.text = this@BaseInputField.text.toString()
            this
        }
    }

    fun setRestoreState(state: Parcelable?) {
        if (state is InnerState) {
            text = if (state.text.isNullOrEmpty()) {
                defaultText?:""
            } else {
                state.text
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val str = if (text.isNullOrEmpty()) {
            defaultText?:""
        } else {
            text
        }
        super.setText(str, type)
    }

    private var editPermission = false

    private fun setEditPermission(isPermitted: Boolean) {
        editPermission = isPermitted
    }

    protected class InnerState : BaseSavedState {
        var text: CharSequence? = null
        var defaultText: CharSequence? = null

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
            defaultText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
            TextUtils.writeToParcel(defaultText, out, flags)
        }
    }

    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(false)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (editPermission) super.addTextChangedListener(watcher)
    }

    fun setOnTextChangeListener(listener: VGSTextView.OnTextChangedListener?) {
        textChangeListener = listener
    }

    inner class InnerTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            textChangeListener?.onTextChange(
                p0.isNullOrEmpty() || p0.toString() == defaultText
            )
        }
    }
}