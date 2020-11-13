package com.verygoodsecurity.vgsshow.widgetnew

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.core.content.res.use
import com.verygoodsecurity.vgsshow.R

abstract class VGSView<T : View> @JvmOverloads internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    protected abstract fun createChildView(): T

    protected abstract fun saveState()

    protected abstract fun restoreState()

    protected val view: T

    var fieldName: String? = null

    var ignoreField: Boolean = false

    init {
        @Suppress("LeakingThis")
        view = createChildView()

        context.obtainStyledAttributes(attrs, R.styleable.VGSView).use {
            fieldName = it.getString(R.styleable.VGSView_fieldName)
            ignoreField = it.getBoolean(R.styleable.VGSView_ignoreField, false)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?) {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, index: Int) {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, width: Int, height: Int) {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child == view) {
            super.addView(child, index, params)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun getChildAt(index: Int): View? {
        return super.getChildAt(index)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        super.addView(view, -1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }
}