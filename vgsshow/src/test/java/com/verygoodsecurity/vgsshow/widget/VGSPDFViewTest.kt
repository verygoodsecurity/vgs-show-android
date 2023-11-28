package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import android.view.View
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VGSPDFViewTest {

    private lateinit var view: VGSPDFView

    @Before
    fun setup() {
        view = VGSPDFView(Robolectric.buildActivity(Activity::class.java).get())
    }

    @Test
    fun `onAttachedToWindow - view has one child`() {
        // Act
        view.onAttachedToWindow()

        // Assert
        Assert.assertEquals(1, view.childCount)
    }

    @Test
    fun `getFieldType - INFO returned`() {
        // Arrange
        val expected = VGSFieldType.PDF

        // Assert
        Assert.assertEquals(expected, view.getFieldType())
    }

    @Test
    fun `ignoreField - true - field ignored`() {
        // Arrange
        val expected = true

        // Act
        view.ignoreField = expected

        // Assert
        Assert.assertEquals(expected, view.ignoreField)
    }

    @Test
    fun `setImportantForAccessibility - child view not attached - importance set to the root view`() {
        // Arrange
        val expected = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        // Act
        view.importantForAccessibility = expected

        // Assert
        Assert.assertEquals(expected, view.importantForAccessibility)
    }

    @Test
    fun `setImportantForAccessibility - child view attached - importance set to the child view`() {
        // Arrange
        val expected = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        // Act
        view.onAttachedToWindow()
        view.importantForAccessibility = expected

        // Assert
        Assert.assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, view.importantForAccessibility)
        Assert.assertEquals(expected, view.getChildView().importantForAccessibility)
    }

    @Test
    fun `setContentDescription - getContentDescription returns correct value`() {
        // Arrange
        val expected = "test"

        // Act
        view.contentDescription = expected

        // Assert
        Assert.assertEquals(view.contentDescription, expected)
    }

    @Test
    fun `setContentPath - getContentPath returns correct value`() {
        // Arrange
        val expected = "test"

        // Act
        view.setContentPath(expected)

        // Assert
        Assert.assertEquals(expected, view.getContentPath())
    }
}