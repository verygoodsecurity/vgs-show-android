package com.verygoodsecurity.vgsshow.widget.text.method

import com.verygoodsecurity.vgsshow.widget.view.internal.text.method.RangePasswordTransformationMethod
import org.junit.Assert.assertEquals
import org.junit.Test

class RangePasswordTransformationMethodTest {

    @Test
    fun test_1() {
        val charSequence = RangePasswordTransformationMethod()
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_2() {
        val charSequence = RangePasswordTransformationMethod(6, 12)
            .getTransformation(EXAMPLE, null)

        assertEquals("424242••••••4242", charSequence.toString())
    }

    @Test
    fun test_3() {
        val charSequence = RangePasswordTransformationMethod(start = -12)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_4() {
        val charSequence = RangePasswordTransformationMethod(start = 77)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_5() {
        val charSequence = RangePasswordTransformationMethod(start = 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("424•••••••••••••", charSequence.toString())
    }

    @Test
    fun test_6() {
        val charSequence = RangePasswordTransformationMethod(7, 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_7() {
        val charSequence = RangePasswordTransformationMethod(end = 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_8() {
        val charSequence = RangePasswordTransformationMethod(end = -3)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_9() {
        val charSequence = RangePasswordTransformationMethod(end = 88)
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_10() {
        val charSequence = RangePasswordTransformationMethod(3, 3)
            .getTransformation(EXAMPLE, null)

        assertEquals("4242424242424242", charSequence.toString())
    }

    @Test
    fun test_11() {
        val charSequence = RangePasswordTransformationMethod(3, 3)
            .getTransformation("", null)

        assertEquals("", charSequence.toString())
    }

    @Test
    fun test_12() {
        val text = "*&&^%\$#@!()_+_\\\\}{}\$"
        val charSequence = RangePasswordTransformationMethod()
            .getTransformation(text, null)

        assertEquals("••••••••••••••••••••", charSequence.toString())
    }

    companion object {
        private const val EXAMPLE = "4242424242424242"
    }
}