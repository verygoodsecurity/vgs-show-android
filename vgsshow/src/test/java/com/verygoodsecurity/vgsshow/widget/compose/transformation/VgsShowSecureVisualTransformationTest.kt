package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class VgsShowSecureVisualTransformationTest {

    @Test
    fun `filter - single range - range masked`() {
        // Arrange
        val transformation = VgsShowSecureVisualTransformation(VGSTextRange(0, 3), '*')

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("****111111111111", result.text.text)
    }

    @Test
    fun `filter - multiple ranges - all ranges masked`() {
        // Arrange
        val transformation = VgsShowSecureVisualTransformation(
            arrayOf(VGSTextRange(0, 3), VGSTextRange(12, 15)),
            '*',
        )

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("****11111111****", result.text.text)
    }

    @Test
    fun `filter - invalid range - range skipped`() {
        // Arrange
        val transformation = VgsShowSecureVisualTransformation(VGSTextRange(10, 3), '*')

        // Act
        val result = transformation.filter(AnnotatedString("test"))

        // Assert
        assertEquals("test", result.text.text)
    }

    @Test
    fun `filter - default symbol - bullet used`() {
        // Arrange
        val transformation = VgsShowSecureVisualTransformation(VGSTextRange(0, 3))

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("••••111111111111", result.text.text)
    }

    @Test
    fun `equals - same ranges and symbol - true returned`() {
        // Arrange
        val a = VgsShowSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')
        val b = VgsShowSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')

        // Assert
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equals - different ranges - false returned`() {
        // Arrange
        val a = VgsShowSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')
        val b = VgsShowSecureVisualTransformation(arrayOf(VGSTextRange(0, 4)), '*')

        // Assert
        assertNotEquals(a, b)
    }
}
