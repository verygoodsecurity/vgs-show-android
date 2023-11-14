package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.text.method.BaseMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VGSTextViewTest {

    private lateinit var view: VGSTextView

    @Before
    fun before() {
        view = VGSTextView(Robolectric.buildActivity(Activity::class.java).get())
    }

    @Test
    fun `onAttachedToWindow - view has one child`() {
        // Act
        view.onAttachedToWindow()

        // Assert
        assertEquals(1, view.childCount)
    }

    @Test
    fun `getFieldType - INFO returned`() {
        // Arrange
        val expected = VGSFieldType.INFO

        // Assert
        assertEquals(expected, view.getFieldType())
    }

    @Test
    fun `ignoreField - true - field ignored`() {
        // Arrange
        val expected = true

        // Act
        view.ignoreField = expected

        // Assert
        assertEquals(expected, view.ignoreField)
    }

    @Test
    fun `setImportantForAccessibility - child view not attached - importance set to the root view`() {
        // Arrange
        val expected = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        // Act
        view.importantForAccessibility = expected

        // Assert
        assertEquals(expected, view.importantForAccessibility)
    }

    @Test
    fun `setImportantForAccessibility - child view attached - importance set to the child view`() {
        // Arrange
        val expected = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        // Act
        view.onAttachedToWindow()
        view.importantForAccessibility = expected

        // Assert
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, view.importantForAccessibility)
        assertEquals(expected, view.getChildView().importantForAccessibility)
    }

    @Test
    fun `setContentDescription - getContentDescription returns correct value`() {
        // Arrange
        val expected = "test"

        // Act
        view.contentDescription = expected

        // Assert
        assertEquals(view.contentDescription, expected)
    }

    @Test
    fun `setContentPath - getContentPath returns correct value`() {
        // Arrange
        val expected = "test"

        // Act
        view.setContentPath(expected)

        // Assert
        assertEquals(expected, view.getContentPath())
    }

    @Test
    fun `setPadding - getPadding return correct values`() {
        // Arrange
        val expectedLeft = 0
        val expectedTop = 1
        val expectedRight = 2
        val expectedBottom = 3

        // Act
        view.setPadding(expectedLeft, expectedTop, expectedRight, expectedBottom)
        view.onAttachedToWindow()

        // Assert
        assertEquals(view.paddingLeft, expectedLeft)
        assertEquals(view.paddingTop, expectedTop)
        assertEquals(view.paddingRight, expectedRight)
        assertEquals(view.paddingBottom, expectedBottom)
    }

    @Test
    fun `setEnabled - true - isEnabled returns correct value`() {
        // Arrange
        val expected = false

        // Act
        view.isEnabled = expected

        // Assert
        assertEquals(expected, view.isEnabled)
    }

    @Test
    fun `setGravity - CENTER - getGravity returns correct value`() {
        // Arrange
        val expected = Gravity.CENTER

        // Act
        view.setGravity(expected)

        // Assert
        assertEquals(expected, view.getChildView().gravity)
    }

    @Test
    fun `setHint - getHint returns correct value`() {
        // Arrange
        val expected = "test"

        // Act
        view.setHint(expected)

        // Assert
        assertEquals(expected, view.getChildView().hint)
    }

    @Test
    fun `setHintTextColor - getHint returns correct value`() {
        // Arrange
        val expected = 0x333334

        // Act
        view.setHintTextColor(expected)

        // Assert
        assertEquals(expected, view.getChildView().hintTextColors.defaultColor)
    }

    @Test
    fun `setInputType - input type password - getInputType returns correct value`() {
        // Arrange
        val expected = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Act
        view.setInputType(expected)

        // Assert
        assertEquals(expected, view.getChildView().inputType)
    }

    @Test
    fun `isPasswordInputType - input type text password - true returned`() {
        // Arrange
        val inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Act
        view.setInputType(inputType)

        // Assert
        assertTrue(view.isPasswordInputType())
    }

    @Test
    fun `isPasswordInputType - input type number password - true returned`() {
        // Arrange
        val inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        // Act
        view.setInputType(inputType)

        // Assert
        assertTrue(view.isPasswordInputType())
    }

    @Test
    fun `isPasswordInputType - input type text - false returned`() {
        // Arrange
        val inputType = InputType.TYPE_CLASS_TEXT

        // Act
        view.setInputType(inputType)

        // Assert
        assertFalse(view.isPasswordInputType())
    }

    @Test
    fun `setTextSize - getTextSize returns correct value`() {
        // Arrange
        val expected = 10f

        // Act
        view.setTextSize(expected)

        // Assert
        assertEquals(expected, view.getChildView().textSize)
    }

    @Test
    fun `setTextSize - unit specified - getTextSize returns correct value`() {
        // Arrange
        val expected = 10f

        // Act
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, expected)

        // Assert
        assertEquals(expected, view.getChildView().textSize)
    }

    @Test
    fun `setTextColor - getTextColors returns correct value`() {
        // Arrange
        val expected = 0xbbbbbc

        // Act
        view.setTextColor(expected)

        // Assert
        assertEquals(expected, view.getChildView().textColors.defaultColor)
    }

    @Test
    fun `setTypeface - SERIF - getTypeface correct value returned`() {
        // Arrange
        val expected = Typeface.SERIF

        // Act
        view.setTypeface(expected)

        // Arrange
        assertEquals(expected, view.getTypeface())
    }

    @Test
    fun `setLetterSpacing - getLetterSpacing correct value returned`() {
        // Arrange
        val expected = 1f

        // Act
        view.setLetterSpacing(expected)

        // Arrange
        assertEquals(expected, view.getChildView().letterSpacing)
    }


    @Test
    fun `setOnTextChangeListener - set non empty text - onTextChange called with  valid parameters`() {
        // Arrange
        val listener = mockk<VGSTextView.OnTextChangedListener>(relaxed = true)
        view.setOnTextChangeListener(listener)

        // Act
        view.setText("test")

        // Assert
        verify(exactly = 1) { listener.onTextChange(view, false) }
    }

    @Test
    fun `setOnTextChangeListener - set empty text - onTextChange called with  valid parameters`() {
        // Arrange
        val listener = mockk<VGSTextView.OnTextChangedListener>(relaxed = true)
        view.setOnTextChangeListener(listener)

        // Act
        view.setText("")

        // Arrange
        verify(exactly = 1) { listener.onTextChange(view, true) }
    }

    @Test
    fun `addTransformationRegex - text formatted`() {
        // Arrange
        val expected = "4111 - 1111 - 1111 - 1111"
        view.addTransformationRegex("(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(), "\$1-\$2-\$3-\$4")
        view.addTransformationRegex("-".toRegex(), " - ")

        // Act
        view.setText("4111111111111111")

        // Assert
        assertEquals(expected, view.getChildView().text.toString())
    }

    @Test
    fun `setOnSecureTextRangeSetListener - setSecureTextRange - onSecureTextRangeSet called`() {
        // Arrange
        val listener = mockk<VGSTextView.OnSetSecureTextRangeSetListener>(relaxed = true)

        // Act
        view.setOnSecureTextRangeSetListener(listener)
        view.setSecureTextRange(VGSTextRange())

        // Assert
        verify(exactly = 1) { listener.onSecureTextRangeSet(view) }
    }

    @Test
    fun `setOnSecureTextRangeSetListener - setSecureTextRange and onViewSubscribed - onSecureTextRangeSet 3 times after subscribe`() {
        // Arrange
        val listener = mockk<VGSTextView.OnSetSecureTextRangeSetListener>(relaxed = true)

        // Act
        view.setSecureTextRange(VGSTextRange())
        view.setSecureTextRange(VGSTextRange())
        view.setSecureTextRange(VGSTextRange())
        view.setOnSecureTextRangeSetListener(listener)
        view.onViewSubscribed()

        // Assert
        verify(exactly = 3) { listener.onSecureTextRangeSet(view) }
    }

    @Test
    fun `setMovementMethod - BaseMovementMethod - getMovementMethod correct value returned`() {
        // Arrange
        val expected = BaseMovementMethod()

        // Act
        view.setMovementMethod(expected)

        // Assert
        assertEquals(expected, view.getChildView().movementMethod)
    }

    @Test
    fun `addOnTextCopyListener - copyToClipboard raw - onTextCopied called with valid parameters`() {
        // Arrange
        val listener = mockk<VGSTextView.OnTextCopyListener>(relaxed = true)

        // Act
        view.setText("test")
        view.addOnCopyTextListener(listener)
        view.copyToClipboard(VGSTextView.CopyTextFormat.RAW)

        // Assert
        verify(exactly = 1) { listener.onTextCopied(view, VGSTextView.CopyTextFormat.RAW) }
    }

    @Test
    fun `addOnTextCopyListener - copyToClipboard formatted - onTextCopied called with valid parameters`() {
        // Arrange
        val listener = mockk<VGSTextView.OnTextCopyListener>(relaxed = true)

        // Act
        view.setText("test")
        view.addOnCopyTextListener(listener)
        view.copyToClipboard(VGSTextView.CopyTextFormat.FORMATTED)

        // Assert
        verify(exactly = 1) { listener.onTextCopied(view, VGSTextView.CopyTextFormat.FORMATTED) }
    }

    @Test
    fun `removeOnTextCopyListener - copyToClipboard raw - onTextCopied not called`() {
        // Arrange
        val listener = mockk<VGSTextView.OnTextCopyListener>()

        // Act
        view.addOnCopyTextListener(listener)
        view.removeOnCopyTextListener(listener)
        view.copyToClipboard(VGSTextView.CopyTextFormat.RAW)

        // Assert
        verify(exactly = 0) { listener.onTextCopied(view, VGSTextView.CopyTextFormat.RAW) }
    }

    @Test
    fun `isEmpty - empty text - true returned`() {
        // Act
        view.setText("")

        // Assert
        assertTrue(view.isEmpty())
    }

    @Test
    fun `isEmpty - not empty text - false returned`() {
        // Act
        view.setText("test")

        // Assert
        assertFalse(view.isEmpty())
    }

    @Test
    fun `clearText - text cleared`() {
        //Act
        view.setText("test")
        view.clearText()

        // Assert
        assertTrue(view.isEmpty())
    }
}