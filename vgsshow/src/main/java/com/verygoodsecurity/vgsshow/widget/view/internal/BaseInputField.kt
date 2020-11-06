package com.verygoodsecurity.vgsshow.widget.view.internal

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.verygoodsecurity.vgsshow.util.extension.transformWithRegex
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.view.internal.text.method.RangePasswordTransformationMethod

internal class BaseInputField(context: Context) : AppCompatTextView(context) {

    internal var fieldName: String? = null

    private var transformationRegex: String? = null
    private var replacement: String = ""

    private var textChangeListener: VGSTextView.OnTextChangedListener? = null

    init {
        setEditPermission(true)
        addTextChangedListener(InnerTextWatcher())
        setEditPermission(false)
        setOnClickListener {
            handlePasswordState()
            (parent as? View)?.callOnClick()
        }

        setOnLongClickListener {
            (parent as? View)?.performLongClick()
            false
        }
    }

    private fun handlePasswordState() {
        if (isPasswordViewType()) {
            transformationMethod = null
        }
    }

    internal fun setPasswordRange(start: Int, end: Int) {
        if (isPasswordViewType()) {
            transformationMethod = RangePasswordTransformationMethod(start, end)
        }
    }

    internal fun getSaveState(state: Parcelable?): BaseSavedState {
        return with(InnerState(state)) {
            this.text = this@BaseInputField.text.toString()
            this
        }
    }

    internal fun setRestoreState(state: Parcelable?) {
        if (state is InnerState) {
            state.text
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val str = transformationRegex?.transformWithRegex(text.toString(), replacement) ?: text
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

    internal fun setOnTextChangeListener(listener: VGSTextView.OnTextChangedListener?) {
        textChangeListener = listener
    }

    inner class InnerTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            textChangeListener?.onTextChange(p0.isNullOrEmpty())
        }
    }

    override fun setTextIsSelectable(selectable: Boolean) {
        super.setTextIsSelectable(selectable && !isPasswordViewType())
    }

    private fun isPasswordViewType(): Boolean {
        return when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> true
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> true
            else -> false
        }
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        setTextIsSelectable(isTextSelectable && !isPasswordViewType())
    }

    internal fun setTransitionRegex(regex: String, text: String?) {
        this.transformationRegex = regex
        replacement = text ?: ""
    }

}