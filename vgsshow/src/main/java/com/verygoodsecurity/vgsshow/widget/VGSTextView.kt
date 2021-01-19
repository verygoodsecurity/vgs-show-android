package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.text.TextUtils
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import com.verygoodsecurity.vgsshow.util.extension.isMarshmallowOrGreater
import com.verygoodsecurity.vgsshow.widget.VGSTextView.CopyTextFormat.FORMATTED
import com.verygoodsecurity.vgsshow.widget.VGSTextView.CopyTextFormat.RAW
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import com.verygoodsecurity.vgsshow.widget.extension.*
import com.verygoodsecurity.vgsshow.widget.view.textview.extension.updateTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange

/**
 * VGS basic View control that displays reviled content to the user.
 */
class VGSTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatTextView>(context, attrs, defStyleAttr) {

    /**
     * Used to determinate should text be replaced with special symbols. For ex. : 4111••••11111111
     */
    var isSecureText: Boolean = false
        set(value) {
            view.transformationMethod = if (value) secureTextTransformMethod else null
            field = value
        }

    /**
     * Symbol that will be used as replacement for secured text.
     */
    var secureTextSymbol: Char = SECURE_SYMBOL
        set(value) {
            this.secureTextTransformMethod.secureSymbol = value
            if (isSecureText) this.view.updateTransformationMethod(secureTextTransformMethod)
            field = value
        }

    private var rawText: String? = null
    private val transformations = mutableListOf<VGSTransformationRegex>()
    private var copyListeners: MutableList<OnTextCopyListener> = mutableListOf()
    private var secureTextListener: OnSetSecureTextRangeSetListener? = null
    private var secureTextListenerCachedInvocationsCounter: Int = 0
    private lateinit var secureTextTransformMethod: SecureTransformationMethod

    init {

        context.getStyledAttributes(attrs, R.styleable.VGSTextView) {
            setGravity(getInt(R.styleable.VGSTextView_gravity, DEFAULT_GRAVITY))

            setHint(getString(R.styleable.VGSTextView_hint))
            setHintTextColor(getColor(R.styleable.VGSTextView_hintTextColor, -1))

            setTextAppearance(getResourceId(R.styleable.VGSTextView_textAppearance, 0))
            setTextSize(getDimension(R.styleable.VGSTextView_textSize, -1f))
            setTextColor(getColor(R.styleable.VGSTextView_textColor, Color.BLACK))
            getFontOrNull(R.styleable.VGSTextView_fontFamily)?.let { setTypeface(it) }
            setTypeface(getTypeface(), getInt(R.styleable.VGSTextView_textStyle, NORMAL))
            setInputType(getInt(R.styleable.VGSTextView_inputType, EditorInfo.TYPE_NULL))
            setSingleLine(getBoolean(R.styleable.VGSTextView_singleLine, false))

            val secureTextStart = getIntOrNull(R.styleable.VGSTextView_secureTextStart)
            val secureTextEnd = getIntOrNull(R.styleable.VGSTextView_secureTextEnd)
            setSecureTextRange(
                arrayOf(
                    VGSTextRange(
                        secureTextStart ?: 0,
                        secureTextEnd ?: Int.MAX_VALUE
                    )
                ), secureTextStart != null || secureTextEnd != null
            )
            secureTextSymbol = getChar(R.styleable.VGSTextView_secureTextSymbol, SECURE_SYMBOL)
            isSecureText = getBoolean(R.styleable.VGSTextView_isSecureText, false)
            isEnabled = getBoolean(R.styleable.VGSTextView_enabled, true)

            if (isLollipopOrGreater) {
                getFloatOrNull(R.styleable.VGSTextView_letterSpacing)?.let {
                    setLetterSpacing(it)
                }
            }
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    /**
     * Gets the current field type of the InputFieldView.
     *
     * @return VGSFieldType
     */
    override fun getFieldType() = VGSFieldType.INFO

    override fun onViewSubscribed() {
        for (i in 0 until secureTextListenerCachedInvocationsCounter) {
            secureTextListener?.onSecureTextRangeSet(this)
        }
    }

    override fun createChildView() = AppCompatTextView(context)

    override fun saveState(state: Parcelable?) = VGSTextViewState(state).apply {
        this.text = view.text?.toString()
    }

    override fun restoreState(state: BaseSavedState) {
        (state as? VGSTextViewState)?.let { view.text = state.text }
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
     * Sets the hint text color.
     *
     * @param color A color value that will be applied
     */
    fun setHintTextColor(@ColorInt color: Int) {
        view.setHintTextColor(color)
    }

    /**
     * Set the type of the content with a constant as defined for input field.
     */
    fun setInputType(inputType: Int) {
        with(view.typeface) {
            view.inputType = inputType
            view.typeface = this
        }
    }

    /**
     * @return true of view input type is password and false otherwise
     */
    fun isPasswordInputType(): Boolean {
        return when (view.inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> true
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> true
            else -> false
        }
    }

    @Suppress("DEPRECATION")
    fun setTextAppearance(@StyleRes styleId: Int) {
        if (isMarshmallowOrGreater) {
            view.setTextAppearance(styleId)
        } else {
            view.setTextAppearance(context, styleId)
        }
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    fun setTextSize(size: Float) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
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
            listener?.onTextChange(this, text.isNullOrEmpty())
        }
    }

    /**
     * Replaces all occurrences of this regular expression in the revealed string with specified [replacement] expression.
     *
     * @param regex Regular expression for transformation revealed data.
     * @param replacement A replacement expression that can include substitutions.
     */
    fun addTransformationRegex(regex: Regex, replacement: String) {
        VGSTransformationRegex(regex, replacement).let { transformations.add(it) }
    }

    /**
     * Used to determinate which part of text should be secured.
     *
     * @param range range of text that should be secured.
     */
    fun setSecureTextRange(range: VGSTextRange) {
        setSecureTextRange(arrayOf(range))
    }

    /**
     * Used to determinate which part of text should be secured.
     *
     * @param ranges array of ranges of text that should be secured.
     */
    fun setSecureTextRange(ranges: Array<VGSTextRange>) {
        setSecureTextRange(ranges, true)
    }

    /**
     * Sets the {@link android.text.method.MovementMethod} for handling arrow key movement
     * for this VGSTextView. This can be null to disallow using the arrow keys to move the
     * cursor or scroll the view.
     *
     * @param movement method, for ex. ScrollingMovementMethod
     */
    fun setMovementMethod(movement: MovementMethod) {
        this.view.movementMethod = movement
    }

    /**
     * When an object of this type is attached to an field, its methods will be called when the text was copied.
     */
    fun addOnCopyTextListener(listener: OnTextCopyListener) {
        this.copyListeners.add(listener)
    }

    /**
     * Remove text copy listener, so it will not be called.
     */
    fun removeOnCopyTextListener(listener: OnTextCopyListener) {
        this.copyListeners.remove(listener)
    }

    /**
     * Copy data to the clipboard from current View. After copying, text trigger [OnTextCopyListener].
     */
    fun copyToClipboard(format: CopyTextFormat = RAW) {
        val textToCopy = when (format) {
            RAW -> rawText
            FORMATTED -> view.text?.toString()
        }
        if (!textToCopy.isNullOrEmpty()) {
            context.copyToClipboard(textToCopy)
            copyListeners.forEach { it.onTextCopied(this, format) }
        }
    }

    /**
     * Check if text is empty.
     *
     * @return true if empty, false otherwise.
     */
    fun isEmpty() = rawText.isNullOrEmpty()

    /**
     * Clear revealed text. Note: This action can't be reverted.
     */
    fun clearText() {
        setText("")
        rawText = null
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
        this.rawText = text?.toString()
        val formattedText = transformations.applyTransformationTo(text.toString())
        view.setText(formattedText, type)
    }

    @VisibleForTesting
    internal fun getChildView() = view

    internal fun setOnSecureTextRangeSetListener(listener: OnSetSecureTextRangeSetListener?) {
        secureTextListener = listener
    }

    private fun setSecureTextRange(ranges: Array<VGSTextRange>, isCustomSecureTextRange: Boolean) {
        if (isCustomSecureTextRange) {
            this.secureTextListener.callOrCache()
        }
        this.secureTextTransformMethod = SecureTransformationMethod(secureTextSymbol, ranges)
        if (isSecureText) this.view.updateTransformationMethod(secureTextTransformMethod)
    }

    private fun OnSetSecureTextRangeSetListener?.callOrCache() {
        if (this == null) {
            secureTextListenerCachedInvocationsCounter += 1
            return
        }
        this.onSecureTextRangeSet(this@VGSTextView)
    }

    private fun MutableList<VGSTransformationRegex>.applyTransformationTo(text: String): String {
        return try {
            var temporaryText = text
            forEach {
                temporaryText = it.regex.replace(temporaryText, it.replacement)
            }
            temporaryText
        } catch (ex: Exception) {
            text
        }
    }

    companion object {

        const val SECURE_SYMBOL = '•'
    }

    internal data class VGSTransformationRegex(
        val regex: Regex,
        val replacement: String
    )

    class VGSTextViewState : BaseSavedState {

        var text: CharSequence? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
        }

        companion object {

            @JvmField
            val CREATOR = object : Parcelable.Creator<VGSTextViewState> {
                override fun createFromParcel(source: Parcel): VGSTextViewState {
                    return VGSTextViewState(source)
                }

                override fun newArray(size: Int): Array<VGSTextViewState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    /**
     * Text format to copy.
     *
     * @property RAW Raw revealed text.
     * @property FORMATTED Formatted text.
     */
    enum class CopyTextFormat {

        RAW,
        FORMATTED
    }

    /**
     * When an object of this type is attached to an [VGSTextView], its method will
     * be called when the text is changed.
     */
    interface OnTextChangedListener {

        /**
         * This method is called to notify you that the text has been changed.
         *
         * @param view The view that was clicked.
         * @param isEmpty If true, then field is have no revealed data.
         */
        fun onTextChange(view: VGSTextView, isEmpty: Boolean)
    }

    /**
     * When an object of this type is attached to an [VGSTextView], its method will
     * be called when user copy content.
     */
    interface OnTextCopyListener {

        /**
         * This method is called after [copyToClipboard] executed.
         *
         * @param view The view that was clicked.
         * @param format Format in which text was copied.
         */
        fun onTextCopied(view: VGSTextView, format: CopyTextFormat)
    }

    internal interface OnSetSecureTextRangeSetListener {

        fun onSecureTextRangeSet(view: VGSTextView)
    }
}