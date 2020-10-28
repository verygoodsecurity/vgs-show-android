package com.verygoodsecurity.vgsshow.widget.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Parcelable
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.view.internal.BaseInputField

internal class FieldStateImpl(
    private val field: BaseInputField
) {

    fun setText(text: CharSequence?) {
        field.text = text
    }

    fun setText(text: CharSequence?, type: TextView.BufferType) {
        field.setText(text, type)
    }

    internal var fieldName: String? = null
        set(value) {
            if (validateFieldName(value)) {
                field = value
                this@FieldStateImpl.field.fieldName = value
            }
        }

    private fun validateFieldName(name: String?): Boolean {
        return !name.isNullOrEmpty()    //TODO improve validation
    }

    var left: Int = 0
        set(value) {
            field = value
            val top = top
            val right = right
            val bottom = bottom
            this@FieldStateImpl.field.setPadding(value, top, right, bottom)
        }

    var start: Int = 0
        set(value) {
            field = value
            val top = top
            val right = right
            val bottom = bottom
            this@FieldStateImpl.field.setPadding(value, top, right, bottom)
        }

    var top: Int = 0
        set(value) {
            field = value
            val left = left
            val right = right
            val bottom = bottom
            this@FieldStateImpl.field.setPadding(left, value, right, bottom)
        }

    var right: Int = 0
        set(value) {
            field = value
            val left = left
            val top = top
            val bottom = bottom
            this@FieldStateImpl.field.setPadding(left, top, value, bottom)
        }

    var end: Int = 0
        set(value) {
            field = value
            val left = left
            val top = top
            val bottom = bottom
            this@FieldStateImpl.field.setPadding(left, top, value, bottom)
        }

    var bottom: Int = 0
        set(value) {
            field = value
            val left = left
            val top = top
            val right = right
            this@FieldStateImpl.field.setPadding(left, top, right, value)
        }

    internal var enabled: Boolean = true
        set(value) {
            field = value
            this@FieldStateImpl.field.isEnabled = value
        }

    internal var textColor: Int = Color.BLACK
        set(value) {
            field = value
            this@FieldStateImpl.field.setTextColor(value)
        }

    internal var textSize: Float = -1f
        set(value) {
            if(value != -1f) {
                field = value
                this@FieldStateImpl.field.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

    internal var isSingleLine: Boolean = true
        set(value) {
            field = value
            this@FieldStateImpl.field.isSingleLine = value
        }

    internal var gravity: Int = Gravity.START or Gravity.CENTER_VERTICAL
        set(value) {
            field = value
            this@FieldStateImpl.field.gravity = value
        }

    internal var typeface: Typeface? = this@FieldStateImpl.field.typeface
        set(value) {
            field = value
            this@FieldStateImpl.field.typeface = value
        }

    fun attachTo(inputFieldView: VGSTextView) {
        inputFieldView.addView(field)
        field.setPadding(left, top, right, bottom)
    }

    fun saveInstanceState(state: Parcelable?): Parcelable? {
        return field.getSaveState(state)
    }

    fun onRestoreInstanceState(state: Parcelable?) {
        field.setRestoreState(state)
    }

    val isViewReady:Boolean
        get() = this@FieldStateImpl.field.parent != null &&
                this@FieldStateImpl.field.parent is VGSTextView
}