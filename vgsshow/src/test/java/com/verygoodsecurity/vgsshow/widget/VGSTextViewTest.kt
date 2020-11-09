package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class VGSTextViewTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSTextView

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSTextView(activity)
    }

    @Test
    fun test_internal_state() {
        view.onAttachedToWindow()
        val internal = view.getState()
        assertNotNull(internal)
        assertTrue(internal!!.isViewReady)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_name() {
        val fn = "test_field_name"

        view.setFieldName(fn)
        assertEquals(view.getFieldName(), fn)

        view.onAttachedToWindow()
        assertEquals(view.getFieldName(), fn)

        val state = view.getState()
        assertEquals(state?.fieldName, fn)
    }

    @Test
    fun test_paddings() {
        view.onAttachedToWindow()

        view.setPadding(30,20,10,0)

        assertEquals(view.paddingLeft, 30)
        assertEquals(view.paddingTop, 20)
        assertEquals(view.paddingRight, 10)
        assertEquals(view.paddingBottom, 0)

        val state = view.getState()
        assertNotNull(state)
        assertEquals(state!!.paddingLeft, 30)
        assertEquals(state.paddingTop, 20)
        assertEquals(state.paddingRight, 10)
        assertEquals(state.paddingBottom, 0)
    }

    @Test
    fun test_gravity() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.setGravity(Gravity.NO_GRAVITY)
        assertEquals(state!!.gravity, Gravity.NO_GRAVITY)

        view.setGravity(Gravity.CENTER)
        assertEquals(state.gravity, Gravity.CENTER)

        view.setGravity(Gravity.TOP or Gravity.END)
        assertEquals(state.gravity, Gravity.TOP or Gravity.END)
    }

    @Test
    fun test_singleline() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.setSingleLine(true)
        assertEquals(state!!.isSingleLine, true)

        view.setSingleLine(false)
        assertEquals(state.isSingleLine, false)
    }

    @Test
    fun test_text_size() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.setTextSize(21f)
        assertEquals(state!!.textSize, 21f)

        view.setTextSize(25f)
        assertEquals(state.textSize, 25f)
    }

    @Test
    fun test_text_color() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.setTextColor(Color.BLUE)
        assertEquals(state!!.textColor, Color.BLUE)

        view.setTextColor(Color.YELLOW)
        assertEquals(state.textColor, Color.YELLOW)
    }

    @Test
    fun test_enabled() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.isEnabled = true
        assertEquals(state!!.enabled, true)

        view.isEnabled = false
        assertEquals(state.enabled, false)
    }

    @Test
    fun testTextStyles() {
        view.onAttachedToWindow()

        val state = view.getState()
        assertNotNull(state)

        view.setTypeface(Typeface.SERIF)
        assertEquals(state!!.typeface, Typeface.SERIF)

        view.setTypeface(Typeface.DEFAULT)
        assertEquals(state.typeface, Typeface.DEFAULT)
    }

    @Test
    fun setHint() {
        val hint = "def text"
        val value = "77"

        view.setHint(hint)
        assertEquals(hint, view.getState()?.hint)

        view.setText(value)
        assertEquals(hint, view.getState()?.hint)

        view.setText("")
        assertEquals(hint, view.getState()?.hint)
    }

    @Test
    fun setOnTextChangeListener() {
        val listener = mock(VGSTextView.OnTextChangedListener::class.java)
        view.setOnTextChangeListener(listener)

        view.setText("123")
        verify(listener).onTextChange(false)

        view.setText("")
        verify(listener).onTextChange(true)

        view.setText("test")
        verify(listener, times(2)).onTextChange(false)
    }

    @Test
    fun setInputType() {
        val inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(inputType)

        assertEquals(inputType, view.getState()?.inputType)
    }

    @Test
    fun setIgnoreView() {
        view.setIgnore(true)
        assertTrue(view.getState()?.ignoreField?:false)

        view.setIgnore(false)
        assertFalse(view.getState()?.ignoreField?:true)
    }
}