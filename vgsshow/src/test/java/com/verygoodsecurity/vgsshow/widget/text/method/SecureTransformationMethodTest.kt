package com.verygoodsecurity.vgsshow.widget.text.method

import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.method.VGSSecureTextOptions
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureTransformationMethodTest {

    private val fullRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE)
    private val options = VGSSecureTextOptions.Builder().build()
    private val ignoreTransformationOptions =
        VGSSecureTextOptions.Builder().setIsIgnoreTransformationRegex(false).build()

    @Test
    fun test_1() {
        val charSequence = SecureTransformationMethod(fullRange, ignoreTransformationOptions)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_2() {
        val range = IntRange(6, 12)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("424242••••••4242", charSequence.toString())
    }

    @Test
    fun test_3() {
        val range = IntRange(-12, 100)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_4() {
        val range = IntRange(101, 100)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_5() {
        val range = IntRange(3, 100)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("424•••••••••••••", charSequence.toString())
    }

    @Test
    fun test_6() {
        val range = IntRange(7, 3)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_7() {
        val range = IntRange(0, 3)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_8() {
        val range = IntRange(0, -3)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_9() {
        val range = IntRange(0, 88)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_10() {
        val range = IntRange(3, 3)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation(EXAMPLE, null)

        assertEquals("4242424242424242", charSequence.toString())
    }

    @Test
    fun test_11() {
        val range = IntRange(3, 3)
        val charSequence = SecureTransformationMethod(range, options)
            .getTransformation("", null)

        assertEquals("", charSequence.toString())
    }

    @Test
    fun test_12() {
        val text = "*&&^%\$#@!()_+_\\\\}{}\$"

        val charSequence = SecureTransformationMethod(fullRange, ignoreTransformationOptions)
            .getTransformation(text, null)

        assertEquals("••••••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_13() {
        val charSequence = SecureTransformationMethod(fullRange, options)
            .getTransformation(EXAMPLE_WITH_TRANSFORMATION, null)

        assertEquals("••••-••••-••••-••••", charSequence.toString())
    }

    @Test
    fun test_14() {
        val ignoreTransformationOptions = VGSSecureTextOptions.Builder()
            .setIsIgnoreTransformationRegex(true)
            .setSecureSymbol('*')
            .build()
        val charSequence = SecureTransformationMethod(fullRange, ignoreTransformationOptions)
            .getTransformation(EXAMPLE_WITH_TRANSFORMATION, null)

        assertEquals("****-****-****-****", charSequence.toString())
    }

    companion object {
        private const val EXAMPLE = "4242424242424242"
        private const val EXAMPLE_WITH_TRANSFORMATION = "4242-4242-4242-4242"
    }
}