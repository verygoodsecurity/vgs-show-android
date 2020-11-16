package com.verygoodsecurity.vgsshow.widget.textview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import com.verygoodsecurity.vgsshow.util.extension.transformWithRegex
import com.verygoodsecurity.vgsshow.widget.VGSView
import com.verygoodsecurity.vgsshow.widget.ViewType
import com.verygoodsecurity.vgsshow.widget.extension.getFloatOrNull
import com.verygoodsecurity.vgsshow.widget.extension.getFontOrNull
import com.verygoodsecurity.vgsshow.widget.extension.getStyledAttributes
import com.verygoodsecurity.vgsshow.widget.textview.method.RangePasswordTransformationMethod

class VGSTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatTextView>(context, attrs, defStyleAttr) {

    private var transformationRegex: String? = null
    private var replacement: String = ""

    init {

        context.getStyledAttributes(attrs, R.styleable.VGSTextView) {
            setGravity(getInt(R.styleable.VGSTextView_gravity, DEFAULT_GRAVITY))
            setHint(getString(R.styleable.VGSTextView_hint))
            setTextSize(getDimension(R.styleable.VGSTextView_textSize, -1f))
            setTextColor(getColor(R.styleable.VGSTextView_textColor, Color.BLACK))
            setTextIsSelectable(getBoolean(R.styleable.VGSTextView_textIsSelectable, false))
            setSingleLine(getBoolean(R.styleable.VGSTextView_singleLine, false))

            getFontOrNull(R.styleable.VGSTextView_fontFamily)?.let { setTypeface(it) }
            setTypeface(getTypeface(), getInt(R.styleable.VGSTextView_textStyle, NORMAL))

            setInputType(getInt(R.styleable.VGSTextView_inputType, EditorInfo.TYPE_NULL))

            isEnabled = getBoolean(R.styleable.VGSTextView_enabled, false)

            setPasswordRange(
                getInt(R.styleable.VGSTextView_passwordStart, -1),
                getInt(R.styleable.VGSTextView_passwordEnd, -1)
            )

            if (isLollipopOrGreater) {
                getFloatOrNull(R.styleable.VGSTextView_letterSpacing)?.let {
                    setLetterSpacing(it)
                }
            }
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    override fun getViewType() = ViewType.INFO

    override fun createChildView() = AppCompatTextView(context)

    override fun saveState(state: Parcelable?): Parcelable? = State(state).apply {
        text = view.text
    }

    override fun restoreState(state: Parcelable?) {
        (state as? State)?.let {
            view.text = it.text
        }
    }

    override fun onChildClick(v: View?) {
        if (isPasswordViewType()) {
            view.transformationMethod = null
        }
        super.onChildClick(v)
    }

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
        super.setPadding(0, 0, 0, 0)
        view.setPadding(left, top, right, bottom)
    }

    /**
     * Returns the top padding of this view.
     *
     * @return the top padding in pixels
     */
    override fun getPaddingTop(): Int {
        return view.paddingTop
    }

    /**
     * Returns the bottom padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the bottom padding in pixels
     */
    override fun getPaddingBottom() = view.paddingBottom

    /**
     * Returns the start padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the start padding in pixels
     */
    override fun getPaddingStart(): Int {
        return view.paddingStart
    }

    /**
     * Returns the end padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the end padding in pixels
     */
    override fun getPaddingEnd() = view.paddingEnd

    /**
     * Returns the left padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the left padding in pixels
     */
    override fun getPaddingLeft() = view.paddingLeft

    /**
     * Returns the right padding of this view.
     * If there are inset and enabled scrollbars, this value may include the space required to display the scrollbars as well.
     *
     * @return the right padding in pixels
     */
    override fun getPaddingRight() = view.paddingRight

    /**
     * Returns the enabled status for this view. The interpretation of the enabled state varies by subclass.
     *
     * @return True if this view is enabled, false otherwise.
     */
    override fun isEnabled(): Boolean = view.isEnabled

    /**
     * Set the enabled state of this view. The interpretation of the enabled state varies by subclass.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    override fun setEnabled(enabled: Boolean) {
        view.isEnabled = enabled
    }

    /**
     * Sets the horizontal alignment of the text and the vertical gravity that will be used when
     * there is extra space in the TextView beyond what is required for the text itself.
     *
     * @param gravity
     */
    fun setGravity(gravity: Int) {
        view.gravity = gravity
    }

    /**
     * Hint text to display when the text is empty.
     */
    fun setHint(text: CharSequence?) {
        view.hint = text
    }

    /**
     * Set the type of the content with a constant as defined for input field.
     */
    fun setInputType(inputType: Int) {
        with(view.typeface) {
            view.inputType = inputType
            view.typeface = this
        }
        setTextIsSelectable(view.isTextSelectable && !isPasswordViewType())
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    fun setTextSize(size: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    /**
     * Set the default text size to a given unit and value.
     * See TypedValue for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    fun setTextSize(unit: Int, size: Float) {
        view.textSize = TypedValue.applyDimension(unit, size, resources.displayMetrics)
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     *
     * @param color A color value that will be applied
     */
    fun setTextColor(@ColorInt color: Int) {
        view.setTextColor(color)
    }

    /**
     * Sets whether or not (default) the content of this view is selectable by the user.
     *
     * Indicates that the content of a non-editable TextView can be selected. Default value is false.
     *
     * @param isSelectable
     */
    fun setTextIsSelectable(isSelectable: Boolean) {
        view.setTextIsSelectable(isSelectable)
    }

    /**
     * If true, sets the properties of this field
     * (number of lines, horizontally scrolling, transformation method) to be for a single-line input.
     *
     * @param singleLine
     */
    fun setSingleLine(singleLine: Boolean) {
        view.isSingleLine = singleLine
    }

    /**
     * Gets the current Typeface that is used to style the text.
     *
     * @return The current Typeface.
     */
    fun getTypeface(): Typeface? = view.typeface

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface This value may be null.
     */
    fun setTypeface(typeface: Typeface) {
        view.typeface = typeface
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
            NORMAL, BOLD, ITALIC -> setTypeface(create(tf, style))
            else -> setTypeface(DEFAULT_BOLD)
        }
    }

    /**
     * Sets text letter-spacing in em units.  Typical values
     * for slight expansion will be around 0.05.  Negative values tighten text.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setLetterSpacing(spacing: Float) {
        view.letterSpacing = spacing
    }

    /**
     * When an object of this type is attached to an field, its methods will be called when the input is changed.
     */
    fun setOnTextChangeListener(listener: OnTextChangedListener?) {
        view.doOnTextChanged { text, _, _, _ ->
            listener?.onTextChange(text.isNullOrEmpty())
        }
    }

    /**
     * Replaces all occurrences of this regular expression in the revealed string with specified [replacement] expression.
     *
     * @param regex Regular expression for transformation revealed data.
     * @param replacement A replacement expression that can include substitutions.
     */
    fun setTransformationRegex(regex: String, replacement: String) {
        this.transformationRegex = regex
        this.replacement = replacement
    }

    /**
     * Used to determinate which part of text should be hided.
     *
     * @param start start of part that should be hided.
     * @param end end of part that should be hided.
     */
    fun setPasswordRange(start: Int, end: Int) {
        if (isPasswordViewType()) {
            view.transformationMethod = RangePasswordTransformationMethod(start, end)
        }
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     */
    internal fun setText(text: CharSequence?) {
        setText(text, TextView.BufferType.NORMAL)
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resId the resource identifier of the string resource to be displayed
     */
    internal fun setText(@StringRes resId: Int) {
        setText(resources.getText(resId), TextView.BufferType.NORMAL)
    }

    /**
     * Sets the text to be displayed using a string resource identifier and the TextView.BufferType.
     *
     * @param resId the resource identifier of the string resource to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     */
    internal fun setText(@StringRes resId: Int, type: TextView.BufferType) {
        setText(resources.getText(resId), type)
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     * @param type a TextView.BufferType which defines whether the text is stored as a static text,
     * styleable/spannable text, or editable text
     */
    internal fun setText(text: CharSequence?, type: TextView.BufferType) {
        with(transformationRegex?.transformWithRegex(text.toString(), replacement) ?: text) {
            view.setText(this, type)
        }
    }

    @VisibleForTesting
    internal fun getChildView() = view

    private fun isPasswordViewType(): Boolean {
        return when (view.inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> true
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> true
            else -> false
        }
    }

    internal class State : BaseSavedState {

        var text: CharSequence? = null

        var defaultText: CharSequence? = null

        companion object {

            @JvmField
            val CREATOR = object : Parcelable.Creator<State> {
                override fun createFromParcel(source: Parcel): State {
                    return State(source)
                }

                override fun newArray(size: Int): Array<State?> {
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

    interface OnTextChangedListener {

        /**
         * This method is called to notify you that the text has been changed.
         *
         * @param isEmpty If true, then field is have no revealed data.
         */
        fun onTextChange(isEmpty: Boolean)
    }
}