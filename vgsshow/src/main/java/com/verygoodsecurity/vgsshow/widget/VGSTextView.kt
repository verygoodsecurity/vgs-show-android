package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.use
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.widget.view.FieldStateImpl
import com.verygoodsecurity.vgsshow.widget.view.internal.BaseInputField

private const val ANALYTICS_TAG = "text"

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


        context.getStyledAttributes(attrs, R.styleable.VGSTextView) {
            for(i in 0 until indexCount) {
                val attr = getIndex(i)
                when(attr) {
                    R.styleable.VGSTextView_gravity -> setGravity(
                        getInt(attr, Gravity.START or Gravity.CENTER_VERTICAL)
                    )
                    R.styleable.VGSTextView_singleLine -> setSingleLine(getBoolean(attr, false))
                    R.styleable.VGSTextView_textSize -> setTextSize(getDimension(attr, -1f))
                    R.styleable.VGSTextView_textStyle -> setupTypeface(this)
                    R.styleable.VGSTextView_fontFamily -> setupFont(this)
                    R.styleable.VGSTextView_textColor -> setTextColor(getColor(attr, Color.BLACK))
                    R.styleable.VGSTextView_hint -> setHint(getString(attr))
                    R.styleable.VGSTextView_fieldName -> setFieldName(getString(attr))
                    R.styleable.VGSTextView_enabled -> isEnabled = getBoolean(attr, false)
                    R.styleable.VGSTextView_inputType -> setInputType(getInt(attr, EditorInfo.TYPE_NULL))
                    R.styleable.VGSTextView_textIsSelectable -> setTextIsSelectable(getBoolean(attr, false))
                    R.styleable.VGSTextView_passwordStart -> setPasswordStart(getInt(attr, -1))
                    R.styleable.VGSTextView_passwordEnd -> setPasswordEnd(getInt(attr, -1))
                }
            }

            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    // TODO: move this function to future abstract base class
    internal fun getAnalyticsTag() = ANALYTICS_TAG

    private fun setPasswordStart(start: Int) {
        fieldState?.passwordStart = start
    }

    private fun setPasswordEnd(end: Int) {
        fieldState?.passwordEnd = end
    }

    fun setPasswordRange(start: Int, end: Int) {
        fieldState?.passwordStart = start
        fieldState?.passwordEnd = end
    }

    private fun setupTypeface(typedArray: TypedArray) {
        getTypeface()?.let {
            setTypeface(it, typedArray.getInt(R.styleable.VGSTextView_textStyle, Typeface.NORMAL))
        }
    }

    private fun setupFont(attrs: TypedArray) {
        val fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            attrs.getFont(R.styleable.VGSTextView_fontFamily)
        } else {
            attrs.getString(R.styleable.VGSTextView_fontFamily)?.run {
                Typeface.create(this, Typeface.NORMAL)
            }
        }
        fontFamily?.let {
            setTypeface(it)
        }
    }

    /**
     * Hint text to display when the text is empty.
     */
    fun setHint(text: CharSequence?) {
        fieldState?.hint = text
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
    fun getFieldName(): String = fieldState?.fieldName ?: ""


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
            fieldState?.attachTo(this)
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
        fieldState?.paddingLeft = left
        fieldState?.paddingTop = top
        fieldState?.paddingRight = right
        fieldState?.paddingBottom = bottom
        super.setPadding(0, 0, 0, 0)
    }


    /**
     * Returns the bottom padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the bottom padding in pixels
     */
    override fun getPaddingBottom(): Int {
        return fieldState?.paddingBottom ?: super.getPaddingBottom()
    }

    /**
     * Returns the end padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the end padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return fieldState?.paddingEnd ?: super.getPaddingEnd()
    }

    /**
     * Returns the left padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the left padding in pixels
     */
    override fun getPaddingLeft(): Int {
        return fieldState?.paddingLeft ?: super.getPaddingLeft()
    }

    /**
     * Returns the right padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the right padding in pixels
     */
    override fun getPaddingRight(): Int {
        return fieldState?.paddingRight ?: super.getPaddingRight()
    }

    /**
     * Returns the start padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the start padding in pixels
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return fieldState?.paddingStart ?: super.getPaddingStart()
    }

    /**
     * Returns the top padding of this view.
     *
     * @return the top padding in pixels
     */
    override fun getPaddingTop(): Int {
        return fieldState?.paddingTop ?: super.getPaddingTop()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return fieldState?.saveInstanceState(super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        fieldState?.onRestoreInstanceState(state)
        super.onRestoreInstanceState(state)
    }

    /**
     * Sets the horizontal alignment of the text and the vertical gravity that will be used when
     * there is extra space in the TextView beyond what is required for the text itself.
     *
     * @param gravity
     */
    fun setGravity(gravity: Int) {
        fieldState?.gravity = gravity
    }

    /**
     * If true, sets the properties of this field
     * (number of lines, horizontally scrolling, transformation method) to be for a single-line input.
     *
     * @param singleLine
     */
    fun setSingleLine(singleLine: Boolean) {
        fieldState?.isSingleLine = singleLine
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    fun setTextSize(size: Float) {
        fieldState?.textSize = size
    }

    /**
     * Set the default text size to a given unit and value.
     * See TypedValue for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    fun setTextSize(unit: Int, size: Float) {
        fieldState?.textSize = TypedValue.applyDimension(
            unit, size, resources.displayMetrics
        )
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     *
     * @param color A color value that will be applied
     */
    fun setTextColor(color: Int) {
        fieldState?.textColor = color
    }


    /**
     * Gets the current Typeface that is used to style the text.
     *
     * @return The current Typeface.
     */
    fun getTypeface(): Typeface? = fieldState?.typeface

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface This value may be null.
     */
    fun setTypeface(typeface: Typeface) {
        fieldState?.typeface = typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the Typeface
     * that you provided does not have all the bits in the style that you specified.
     *
     * @param tf This value may be null.
     * @param style Value is Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, or Typeface.BOLD_ITALIC
     */
    fun setTypeface(tf: Typeface?, style: Int) {
        when (style) {
            Typeface.NORMAL -> fieldState?.typeface = Typeface.create(tf, style)
            1 -> fieldState?.typeface = Typeface.create(tf, Typeface.BOLD)
            2 -> fieldState?.typeface = Typeface.create(tf, Typeface.ITALIC)
            else -> fieldState?.typeface = Typeface.DEFAULT_BOLD
        }
    }

    /**
     * Returns the enabled status for this view. The interpretation of the enabled state varies by subclass.
     *
     * @return True if this view is enabled, false otherwise.
     */
    override fun isEnabled(): Boolean = fieldState?.enabled ?: false

    /**
     * Set the enabled state of this view. The interpretation of the enabled state varies by subclass.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    override fun setEnabled(enabled: Boolean) {
        fieldState?.enabled = enabled
    }

    /**
     * Sets whether or not (default) the content of this view is selectable by the user.
     *
     * Indicates that the content of a non-editable TextView can be selected. Default value is false.
     *
     * @param isSelectable
     */
    fun setTextIsSelectable(isSelectable: Boolean) {
        fieldState?.isSelectable = isSelectable
    }

    /**
     * When an object of this type is attached to an field, its methods will be called when the input is changed.
     */
    fun setOnTextChangeListener(listener: OnTextChangedListener?) {
        fieldState?.setOnTextChangeListener(listener)
    }

    /**
     * Replaces all occurrences of this regular expression in the revealed string with specified [replacement] expression.
     *
     * @param regex Regular expression for transformation revealed data.
     * @param replacement A replacement expression that can include substitutions.
     */
    fun setTransitionRegex(regex:String, replacement:String) {
        fieldState?.setTransitionRegex(regex, replacement)
    }

    interface OnTextChangedListener {

        /**
         * This method is called to notify you that the text has been changed.
         *
         * @param isEmpty If true, then field is have no revealed data.
         */
        fun onTextChange(isEmpty: Boolean)
    }

    /**
     * Set the type of the content with a constant as defined for input field.
     */
    fun setInputType(inputType: Int) {
        fieldState?.inputType = inputType
    }

    fun setIgnore(ignoreField: Boolean) {
        fieldState?.ignoreField = ignoreField
    }

    fun isIgnored(): Boolean = fieldState?.ignoreField ?: false
}

private fun Context.getStyledAttributes(
    attributeSet: AttributeSet?,
    styleArray: IntArray, block: TypedArray.() -> Unit
) = this
    .obtainStyledAttributes(attributeSet, styleArray, 0, 0)
    .use(block)
