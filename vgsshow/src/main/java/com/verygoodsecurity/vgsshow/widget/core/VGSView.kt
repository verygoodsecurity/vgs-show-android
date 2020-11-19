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
import androidx.core.content.res.use
import com.verygoodsecurity.vgsshow.R

@Suppress("LeakingThis")
abstract class VGSView<T : View> @JvmOverloads internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    abstract fun getFieldType(): VGSFieldType

    protected abstract fun createChildView(): T

    protected abstract fun saveState(state: Parcelable?): BaseSavedState?

    protected abstract fun restoreState(state: BaseSavedState)

    protected val view: T = createChildView().apply { this.id = View.generateViewId() }

    private var fieldName: String? = null

    var ignoreField: Boolean = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.VGSView).use {
            fieldName = it.getString(R.styleable.VGSView_fieldName)
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

    open fun onChildClick(v: View?) {
        callOnClick()
    }

    open fun onChildLongClick(v: View?): Boolean {
        performLongClick()
        return false
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param name the name of the field
     */
    fun setFieldName(name: String?) {
        this.fieldName = name
    }

    /**
     * Sets the text to be used for data transfer between field and VGS proxy. Usually,
     * it is similar to field-name in JSON path in your inbound route filters.
     *
     * @param id the resource identifier of the field name
     */
    @Throws(Resources.NotFoundException::class)
    fun setFieldName(@StringRes id: Int) {
        this.fieldName = resources.getString(id)
    }

    /**
     * Return the text that is using for data transfer between VGS proxy and field.
     *
     * @return The text used by the field.
     */
    fun getFieldName(): String = fieldName ?: ""

    companion object {

        internal const val DEFAULT_GRAVITY = Gravity.START or Gravity.CENTER_VERTICAL
    }
}