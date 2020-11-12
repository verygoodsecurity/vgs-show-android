package com.verygoodsecurity.vgsshow.widget.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Parcelable
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.view.internal.BaseInputField

internal class FieldStateImpl(
    private val field: BaseInputField
) {

    internal var passwordStart: Int = -1
        set(value) {
            field = value
            this@FieldStateImpl.field.setPasswordRange(value, passwordEnd)
        }

    internal var passwordEnd: Int = -1
        set(value) {
            field = value
            this@FieldStateImpl.field.setPasswordRange(passwordStart, value)
        }

    internal var inputType: Int = InputType.TYPE_NULL
        set(value) {
            field = value
            this@FieldStateImpl.field.inputType = value
        }

    internal var hint: CharSequence? = null
        set(value) {
            field = value
            this@FieldStateImpl.field.hint = value
        }


    fun setText(text: CharSequence?) {
        field.text = text
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getText() = field.text

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

    var paddingLeft: Int = 0
        set(value) {
            field = value
            val top = paddingTop
            val right = paddingRight
            val bottom = paddingBottom
            this@FieldStateImpl.field.setPadding(value, top, right, bottom)
        }

    var paddingStart: Int = 0
        set(value) {
            field = value
            val top = paddingTop
            val right = paddingRight
            val bottom = paddingBottom
            this@FieldStateImpl.field.setPadding(value, top, right, bottom)
        }

    var paddingTop: Int = 0
        set(value) {
            field = value
            val left = paddingLeft
            val right = paddingRight
            val bottom = paddingBottom
            this@FieldStateImpl.field.setPadding(left, value, right, bottom)
        }

    var paddingRight: Int = 0
        set(value) {
            field = value
            val left = paddingLeft
            val top = paddingTop
            val bottom = paddingBottom
            this@FieldStateImpl.field.setPadding(left, top, value, bottom)
        }

    var paddingEnd: Int = 0
        set(value) {
            field = value
            val left = paddingLeft
            val top = paddingTop
            val bottom = paddingBottom
            this@FieldStateImpl.field.setPadding(left, top, value, bottom)
        }

    var paddingBottom: Int = 0
        set(value) {
            field = value
            val left = paddingLeft
            val top = paddingTop
            val right = paddingRight
            this@FieldStateImpl.field.setPadding(left, top, right, value)
        }

    internal var enabled: Boolean = true
        set(value) {
            field = value
            this@FieldStateImpl.field.isEnabled = value
        }

    internal var isSelectable: Boolean = false
        set(value) {
            field = value
            this@FieldStateImpl.field.setTextIsSelectable(value)
        }

    internal var textAppearance: Int = 0
        set(value) {
            field = value
            this@FieldStateImpl.field.setTextAppearance(value)
        }

    internal var textColor: Int = Color.BLACK
        set(value) {
            field = value
            this@FieldStateImpl.field.setTextColor(value)
        }

    internal var textSize: Float = -1f
        set(value) {
            if (value != -1f) {
                field = value
                this@FieldStateImpl.field.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

    internal var ignoreField: Boolean = false
        set(value) {
            field = value
            this@FieldStateImpl.field.ignoreField = value
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
        field.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    fun saveInstanceState(state: Parcelable?): Parcelable? {
        return field.getSaveState(state)
    }

    fun onRestoreInstanceState(state: Parcelable?) {
        field.setRestoreState(state)
    }

    val isViewReady: Boolean
        get() = this@FieldStateImpl.field.parent != null &&
                this@FieldStateImpl.field.parent is VGSTextView

    fun setOnTextChangeListener(listener: VGSTextView.OnTextChangedListener?) {
        field.setOnTextChangeListener(listener)
    }

    fun setTransformationRegex(regex: String, textToReplace: String) {
        field.setTransformationRegex(regex, textToReplace)
    }
}