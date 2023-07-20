package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
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
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_name() {
        val fn = "test_field_name"

        view.setContentPath(fn)
        assertEquals(view.getContentPath(), fn)

        view.onAttachedToWindow()
        assertEquals(view.getContentPath(), fn)
    }

    @Test
    fun test_paddings() {
        view.setPadding(30, 20, 10, 0)
        view.onAttachedToWindow()
        assertEquals(view.paddingLeft, 30)
        assertEquals(view.paddingTop, 20)
        assertEquals(view.paddingRight, 10)
        assertEquals(view.paddingBottom, 0)
    }

    @Test
    fun test_gravity() {
        view.setGravity(Gravity.CENTER)
        assertEquals(view.getChildView().gravity, Gravity.CENTER)

        view.setGravity(Gravity.TOP or Gravity.END)
        assertEquals(view.getChildView().gravity, Gravity.TOP or Gravity.END)
    }

    @Test
    fun test_text_size() {
        view.setTextSize(21f)
        assertEquals(view.getChildView().textSize, 21f)

        view.setTextSize(25f)
        assertEquals(view.getChildView().textSize, 25f)
    }

    @Test
    fun test_text_color() {
        view.setTextColor(Color.BLUE)
        assertEquals(view.getChildView().textColors.defaultColor, Color.BLUE)

        view.setTextColor(Color.YELLOW)
        assertEquals(view.getChildView().textColors.defaultColor, Color.YELLOW)
    }

    @Test
    fun test_enabled() {
        view.isEnabled = true
        assertEquals(view.getChildView().isEnabled, true)

        view.isEnabled = false
        assertEquals(view.getChildView().isEnabled, false)
    }

    @Test
    fun testTextStyles() {
        view.setTypeface(Typeface.SERIF)
        assertEquals(view.getChildView().typeface, Typeface.SERIF)

        view.setTypeface(Typeface.DEFAULT)
        assertEquals(view.getChildView().typeface, Typeface.DEFAULT)
    }

    @Test
    fun setHint() {
        val hint = "def text"
        val value = "77"

        view.setHint(hint)
        assertEquals(hint, view.getChildView().hint)

        view.setText(value)
        assertEquals(hint, view.getChildView().hint)

        view.setText("")
        assertEquals(hint, view.getChildView().hint)
    }

    @Test
    fun setOnTextChangeListener() {
        val listener = mock(VGSTextView.OnTextChangedListener::class.java)
        view.setOnTextChangeListener(listener)

        view.setText("123")
        verify(listener).onTextChange(view, false)

        view.setText("")
        verify(listener).onTextChange(view, true)

        view.setText("test")
        verify(listener, times(2)).onTextChange(view, false)
    }

    @Test
    fun addOnTextCopyListener_listenerCalled() {
        val listener = mock(VGSTextView.OnTextCopyListener::class.java)
        view.setText("test")
        view.addOnCopyTextListener(listener)

        view.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        verify(listener, times(1)).onTextCopied(view, VGSTextView.CopyTextFormat.RAW)

        view.copyToClipboard(VGSTextView.CopyTextFormat.FORMATTED)
        verify(listener, times(1)).onTextCopied(view, VGSTextView.CopyTextFormat.FORMATTED)
    }

    @Test
    fun removeOnTextCopyListener_listenerNotCalled() {
        val listener = mock(VGSTextView.OnTextCopyListener::class.java)

        view.addOnCopyTextListener(listener)
        view.removeOnCopyTextListener(listener)
        view.copyToClipboard(VGSTextView.CopyTextFormat.FORMATTED)

        verify(listener, times(0)).onTextCopied(view, VGSTextView.CopyTextFormat.FORMATTED)
    }

    @Test
    fun setInputType() {
        val inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(inputType)

        assertEquals(inputType, view.getChildView().inputType)
    }

    @Test
    fun isPasswordInputType() {
        val inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(inputType)

        assertTrue(view.isPasswordInputType())
    }

    @Test
    fun setIgnoreView() {
        view.ignoreField = true
        assertTrue(view.ignoreField)

        view.ignoreField = false
        assertFalse(view.ignoreField)
    }

    @Test
    fun setHintTextColor() {
        view.setHintTextColor(Color.CYAN)

        assertEquals(view.getChildView().hintTextColors.defaultColor, Color.CYAN)
    }

    @Test
    fun addTransformationRegex() {
        val result = "4111 - 1111 - 1111 - 1111"

        view.addTransformationRegex("(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(), "\$1-\$2-\$3-\$4")
        view.addTransformationRegex("-".toRegex(), " - ")
        view.setText("4111111111111111")

        assertEquals(result, view.getChildView().text.toString())
    }

    @Test
    fun clearText() {
        view.setText("4111111111111111")

        view.clearText()

        assertTrue(view.getChildView().text.isEmpty())
    }

    @Test
    fun setIsSecureText() {
        view.isSecureText = true
        assertTrue(view.isSecureText)

        view.isSecureText = false
        assertFalse(view.isSecureText)

        view.isSecureText = true
        assertTrue(view.isSecureText)
    }

    @Test
    fun setOnSecureTextRangeSetListenerCalled() {
        val listener = mock(VGSTextView.OnSetSecureTextRangeSetListener::class.java)

        view.setOnSecureTextRangeSetListener(listener)
        view.setSecureTextRange(VGSTextRange())

        verify(listener, times(1)).onSecureTextRangeSet(view)
    }

    @Test
    fun setOnSecureTextRangeSetListenerCachedInvocationsCalledOnViewSubscribed() {
        val listener = mock(VGSTextView.OnSetSecureTextRangeSetListener::class.java)

        view.setSecureTextRange(VGSTextRange())
        view.setSecureTextRange(VGSTextRange())
        view.setSecureTextRange(VGSTextRange())

        view.setOnSecureTextRangeSetListener(listener)
        view.onViewSubscribed()

        verify(listener, times(3)).onSecureTextRangeSet(view)
    }

    @Test
    fun test_accessibility() {
        view.contentDescription = "text"
        assertEquals(view.contentDescription, "text")
    }
}