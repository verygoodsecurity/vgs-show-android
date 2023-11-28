package com.verygoodsecurity.vgsshow.widget.extension

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ViewGroupKtTest {

    private lateinit var view: VGSViewFake

    @Before
    fun before() {
        view = VGSViewFake(Robolectric.buildActivity(Activity::class.java).get())
    }

    @Test
    fun `hasView - onAttachToWindow called - returns true`() {
        // Act
        view.onAttachedToWindow()

        // Assert
        assertTrue(view.hasView())
    }

    @Test
    fun `hasView - onAttachToWindow not called - returns false`() {
        // Assert
        assertFalse(view.hasView())
    }
}

class VGSViewFake constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<View>(context, attrs, defStyleAttr) {

    override fun getFieldType(): VGSFieldType = VGSFieldType.INFO

    override fun createChildView(attrs: AttributeSet?, defStyleAttr: Int): View {
        return View(context)
    }

    override fun saveState(state: Parcelable?): BaseSavedState? {
        return null
    }

    override fun restoreState(state: BaseSavedState) {
        // NO-OP
    }
}