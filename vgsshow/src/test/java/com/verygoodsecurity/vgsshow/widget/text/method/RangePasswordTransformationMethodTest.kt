package com.verygoodsecurity.vgsshow.widget.text.method

import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import org.junit.Assert.assertEquals
import org.junit.Test

class RangePasswordTransformationMethodTest {

    @Test
    fun test_1() {
        val charSequence = SecureTransformationMethod()
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_2() {
        val charSequence = SecureTransformationMethod(6, 12)
            .getTransformation(EXAMPLE, null)

        assertEquals("424242••••••4242", charSequence.toString())
    }

    @Test
    fun test_3() {
        val charSequence = SecureTransformationMethod(start = -12)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_4() {
        val charSequence = SecureTransformationMethod(start = 77)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_5() {
        val charSequence = SecureTransformationMethod(start = 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("424•••••••••••••", charSequence.toString())
    }

    @Test
    fun test_6() {
        val charSequence = SecureTransformationMethod(7, 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_7() {
        val charSequence = SecureTransformationMethod(end = 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_8() {
        val charSequence = SecureTransformationMethod(end = -3)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_9() {
        val charSequence = SecureTransformationMethod(end = 88)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_10() {
        val charSequence = SecureTransformationMethod(3, 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("4242424242424242", charSequence.toString())
    }

    @Test
    fun test_11() {
        val charSequence = SecureTransformationMethod(3, 3)
            .getTransformation("", null)

        assertEquals("", charSequence.toString())
    }

    @Test
    fun test_12() {
        val text = "*&&^%\$#@!()_+_\\\\}{}\$"
        val charSequence = SecureTransformationMethod()
            .getTransformation(text, null)

        assertEquals("••••••••••••••••••••", charSequence.toString())
    }

    companion object {
        private const val EXAMPLE = "4242424242424242"
    }
}