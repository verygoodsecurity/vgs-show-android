package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.widget.view.FieldStateImpl
import com.verygoodsecurity.vgsshow.widget.view.internal.BaseInputField

class VGSTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isAttachPermitted = true

    private var fieldState: FieldStateImpl? = null

    init {
        val field = BaseInputField(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        fieldState = FieldStateImpl(field)


        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.VGSTextView,
                0, 0
        ).apply {
            try {
                val text = getString(R.styleable.VGSTextView_text)
                val fieldName = getString(R.styleable.VGSTextView_fieldName)

                setText(text)
                setFieldName(fieldName)

                setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            } finally {
                recycle()
            }
        }
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     */
    internal fun setText(text: CharSequence?) {
        fieldState?.setText(text)
    }

    /**
     * Sets the text to be displayed using a string resource identifier and the TextView.BufferType.
     *
     * @param resId the resource identifier of the string resource to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     */
    internal fun setText(@StringRes resId: Int, type: TextView.BufferType) {
        fieldState?.setText(context.resources.getText(resId), type)
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resId the resource identifier of the string resource to be displayed
     */
    internal fun setText(@StringRes resId: Int) {
        fieldState?.setText(context.resources.getText(resId))
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param fieldName the name of the field
     */
    fun setFieldName(fieldName: CharSequence?) {
        fieldState?.fieldName = fieldName?.toString()
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param resId the resource identifier of the field name
     */
    fun setFieldName(@StringRes resId: Int) {
        fieldState?.fieldName = context.getString(resId)
    }

    /**
     * Return the text that is using for data transfer between VGS proxy and field.
     *
     * @return The text used by the field.
     */
    open fun getFieldName(): String? = fieldState?.fieldName


    override fun onDetachedFromWindow() {
        if (childCount > 0) removeAllViews()
        super.onDetachedFromWindow()
    }


    override fun addView(child: View?) {
        if (childCount == 0 && child is BaseInputField) {
            super.addView(child)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.addView(child, params)
        }
    }

    override fun addView(child: View?, index: Int) {
        if (isAttachPermitted) {
            super.addView(child, index)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if (isAttachPermitted) {
            super.addView(child, width, height)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.addView(child, index, params)
        }
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.attachViewToParent(child, index, params)
        }
    }

    public override fun onAttachedToWindow() {
        if (isAttachPermitted) {
            super.onAttachedToWindow()
            setAddStatesFromChildren(true)
            fieldState?.attach(this)
            isAttachPermitted = false
        }
    }

    @VisibleForTesting
    internal fun getState() = fieldState

    /**
     * Sets the padding.
     * The view may add on the space required to display the scrollbars, depending on the style and visibility of the scrollbars.
     * So the values returned from getPaddingLeft(), getPaddingTop(), getPaddingRight() and getPaddingBottom()
     * may be different from the values set in this call.
     *
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        fieldState?.left = left
        fieldState?.top = top
        fieldState?.right = right
        fieldState?.bottom = bottom
        super.setPadding(0, 0, 0, 0)
    }

}