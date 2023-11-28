package com.verygoodsecurity.vgsshow.widget.core

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.core.content.res.use
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.widget.extension.hasView

@Suppress("LeakingThis")
abstract class VGSView<T : View> @JvmOverloads internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Returns type of the field.
     *
     * @return [VGSFieldType]
     */
    abstract fun getFieldType(): VGSFieldType

    protected abstract fun createChildView(attrs: AttributeSet?, defStyleAttr: Int): T

    protected abstract fun saveState(state: Parcelable?): BaseSavedState?

    protected abstract fun restoreState(state: BaseSavedState)

    protected val view: T = createChildView(attrs, defStyleAttr).apply {
        this.id = View.generateViewId()
    }

    private var contentPath: String? = null

    var ignoreField: Boolean = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.VGSView).use {
            contentPath = it.getString(R.styleable.VGSView_contentPath)
            ignoreField = it.getBoolean(R.styleable.VGSView_ignoreField, false)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?) {
        if (child == view) super.addView(child)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, index: Int) {
        if (child == view) super.addView(child, index)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, width: Int, height: Int) {
        if (child == view) super.addView(child, width, height)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child == view) super.addView(child, params)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child == view) super.addView(child, index, params)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun getChildAt(index: Int): View? = super.getChildAt(index)

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAddStatesFromChildren(true)
        super.addView(view, -1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        view.setOnClickListener { onChildClick(it) }
        view.setOnLongClickListener { onChildLongClick(it) }
        view.isLongClickable = false
        view.contentDescription = this.contentDescription
        view.importantForAccessibility = this.importantForAccessibility
        super.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        super.removeAllViews()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return saveState(super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? BaseSavedState).let { saveState ->
            super.onRestoreInstanceState(saveState?.superState)
            saveState?.let { restoreState(it) }
        }
    }

    protected open fun onChildClick(v: View?) {
        callOnClick()
    }

    protected open fun onChildLongClick(v: View?): Boolean {
        performLongClick()
        return true
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param name The name of the field.
     */
    fun setContentPath(name: String?) {
        this.contentPath = name
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param id The resource identifier of the field name.
     */
    @Throws(Resources.NotFoundException::class)
    fun setContentPath(@StringRes id: Int) {
        this.contentPath = resources.getString(id)
    }

    /**
     * Return the text that is using for data transfer between VGS proxy and field.
     *
     * @return The text used by the field.
     */
    fun getContentPath(): String = contentPath ?: ""

    /**
     * Sets how to determine whether this view is important for accessibility
     * which is if it fires accessibility events and if it is reported to
     * accessibility services that query the screen.
     *
     * @param mode How to determine whether this view is important for accessibility.
     *
     * @attr [android.R.attr.importantForAccessibility]
     *
     * @see View.IMPORTANT_FOR_ACCESSIBILITY_YES
     * @see View.IMPORTANT_FOR_ACCESSIBILITY_NO
     * @see View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
     * @see View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
     */
    override fun setImportantForAccessibility(mode: Int) {
        if (hasView()) {
            this.view.importantForAccessibility = mode
            super.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO)
        } else {
            super.setImportantForAccessibility(mode)
        }
    }

    /**
     * Sets the [View]'s content description.
     * <p>
     * A content description briefly describes the view and is primarily used
     * for accessibility support to determine how a view should be presented to
     * the user. In the case of a view with no textual representation, such as
     * [android.widget.ImageButton], a useful content description
     * explains what the view does. For example, an image button with a phone
     * icon that is used to place a call may use "Call" as its content
     * description. An image of a floppy disk that is used to save a file may
     * use "Save".
     *
     * @param contentDescription The content description.
     * @see getContentDescription
     * @attr [android.R.attr.contentDescription]
     */
    override fun setContentDescription(contentDescription: CharSequence?) {
        if (hasView()) this.view.contentDescription = contentDescription
        super.setContentDescription(contentDescription)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getChildView() = view

    companion object {

        internal const val DEFAULT_GRAVITY = Gravity.START or Gravity.CENTER_VERTICAL
    }
}