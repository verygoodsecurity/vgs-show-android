package com.verygoodsecurity.vgsshow.widget.core

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RestrictTo


abstract class VGSRenderView<T : View> @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX) @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<T>(context, attrs, defStyleAttr) {

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    abstract fun render(bytes: ByteArray)
}