package com.verygoodsecurity.vgsshow.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class VgsShowSecureVisualTransformationTest {

    @Test
    fun `filterUnmasked - secure - text unchanged`() {
        val transformation = VgsSecureVisualTransformation(VGSTextRange(0, 3), '*')

        val result = transformation.filterUnmasked(AnnotatedString("4111111111111111"))

        assertEquals("4111111111111111", result.text)
    }

    @Test
    fun `filterUnmasked - regex - formatting applied`() {
        val transformation = VgsRegexVisualTransformation(Regex("(\\d{4})(?=\\d)"), "$1 ")

        val result = transformation.filterUnmasked(AnnotatedString("41111111"))

        assertEquals("4111 1111", result.text)
    }

    @Test
    fun `filterUnmasked - chain - formatted but not masked`() {
        val transformation = VgsChainedVisualTransformation(
            listOf(
                VgsRegexVisualTransformation(Regex("(\\d{4})(?=\\d)"), "$1 "),
                VgsSecureVisualTransformation(VGSTextRange(0, 3), '*'),
            )
        )
        val text = AnnotatedString("41111111")

        assertEquals("4111 1111", transformation.filterUnmasked(text).text)
        assertEquals("**** 1111", transformation.filter(text).text)
    }

    @Test
    fun `filterUnmasked - none - text unchanged`() {
        val result = VgsVisualTransformation.None.filterUnmasked(AnnotatedString("abc"))

        assertEquals("abc", result.text)
    }

    @Test
    fun `filter - single range - range masked`() {
        // Arrange
        val transformation = VgsSecureVisualTransformation(VGSTextRange(0, 3), '*')

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("****111111111111", result.text)
    }

    @Test
    fun `filter - multiple ranges - all ranges masked`() {
        // Arrange
        val transformation = VgsSecureVisualTransformation(
            arrayOf(VGSTextRange(0, 3), VGSTextRange(12, 15)),
            '*',
        )

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("****11111111****", result.text)
    }

    @Test
    fun `filter - invalid range - range skipped`() {
        // Arrange
        val transformation = VgsSecureVisualTransformation(VGSTextRange(10, 3), '*')

        // Act
        val result = transformation.filter(AnnotatedString("test"))

        // Assert
        assertEquals("test", result.text)
    }

    @Test
    fun `filter - default symbol - bullet used`() {
        // Arrange
        val transformation = VgsSecureVisualTransformation(VGSTextRange(0, 3))

        // Act
        val result = transformation.filter(AnnotatedString("4111111111111111"))

        // Assert
        assertEquals("••••111111111111", result.text)
    }

    @Test
    fun `equals - same ranges and symbol - true returned`() {
        // Arrange
        val a = VgsSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')
        val b = VgsSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')

        // Assert
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equals - different ranges - false returned`() {
        // Arrange
        val a = VgsSecureVisualTransformation(arrayOf(VGSTextRange(0, 3)), '*')
        val b = VgsSecureVisualTransformation(arrayOf(VGSTextRange(0, 4)), '*')

        // Assert
        assertNotEquals(a, b)
    }
}
