package com.verygoodsecurity.vgsshow.widget.text.method

import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.method.VGSSecureTextOptions
import com.verygoodsecurity.vgsshow.widget.view.textview.method.VGSSecureTextRange
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureTransformationMethodTest {

    private val fullRange = VGSSecureTextRange(Int.MIN_VALUE, Int.MAX_VALUE)
    private val options = VGSSecureTextOptions.Builder().build()

    @Test
    fun test_1() {
        val charSequence = SecureTransformationMethod(arrayOf(fullRange), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_2() {
        val range = VGSSecureTextRange(6, 12)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("424242••••••4242", charSequence.toString())
    }

    @Test
    fun test_3() {
        val range = VGSSecureTextRange(-12, 100)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_4() {
        val range = VGSSecureTextRange(101, 100)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_5() {
        val range = VGSSecureTextRange(3, 100)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("424•••••••••••••", charSequence.toString())
    }

    @Test
    fun test_6() {
        val range = VGSSecureTextRange(7, 3)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_7() {
        val range = VGSSecureTextRange(0, 3)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_8() {
        val range = VGSSecureTextRange(0, -3)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_9() {
        val range = VGSSecureTextRange(0, 88)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_10() {
        val range = VGSSecureTextRange(3, 3)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("4242424242424242", charSequence.toString())
    }

    @Test
    fun test_11() {
        val range = VGSSecureTextRange(3, 3)
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation("", null)

        assertEquals("", charSequence.toString())
    }

    @Test
    fun test_12() {
        val range = VGSSecureTextRange(0, 3)
        val rangeTwo = VGSSecureTextRange(5, 7)
        val charSequence = SecureTransformationMethod(arrayOf(range, rangeTwo), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("•••24••242424242", charSequence.toString())
    }

    @Test
    fun test_13() {
        val range = VGSSecureTextRange(0, 3)
        val options = VGSSecureTextOptions.Builder().setSecureSymbol('%').build()
        val charSequence = SecureTransformationMethod(arrayOf(range), options)
            .getTransformation(EXAMPLE, null)

        assertEquals("%%%2424242424242", charSequence.toString())
    }

    companion object {
        private const val EXAMPLE = "4242424242424242"
    }
}