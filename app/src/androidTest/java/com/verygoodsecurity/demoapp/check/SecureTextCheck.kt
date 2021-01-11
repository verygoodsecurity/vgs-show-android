package com.verygoodsecurity.demoapp.check

import android.view.View
import android.widget.TextView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import org.junit.Assert.assertEquals

class SecureTextCheck constructor(private val expectedText: String) : ViewAssertion {

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        val parentView = (view as? VGSView<*>) ?: throw IllegalArgumentException("View is not secure view!")
        val textChildView = (parentView.getChildAt(0) as? TextView) ?: throw IllegalStateException("No views with text!")
        assertEquals(expectedText, textChildView.text.toString())
    }
}