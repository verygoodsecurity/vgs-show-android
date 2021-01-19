package com.verygoodsecurity.vgsshow.widget.text.method

import com.verygoodsecurity.vgsshow.widget.view.textview.method.SecureTransformationMethod
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureTransformationMethodTest {

    private val fullRange = VGSTextRange()
    private val secureSymbol =  '•'

    @Test
    fun test_1() {
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(fullRange))
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_2() {
        val range = VGSTextRange(end = 2)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_3() {
        val range = VGSTextRange(0)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_4() {
        val range = VGSTextRange(-1, 5)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals(EXAMPLE, charSequence.toString())
    }

    @Test
    fun test_5() {
        val range = VGSTextRange(0, -1)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals(EXAMPLE, charSequence.toString())
    }


    @Test
    fun test_6() {
        val range = VGSTextRange(5,4)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals(EXAMPLE, charSequence.toString())
    }

    @Test
    fun test_7() {
        val range = VGSTextRange(100, 5)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals(EXAMPLE, charSequence.toString())
    }

    @Test
    fun test_8() {
        val range = VGSTextRange(0, 100)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals("••••••••••••••••", charSequence.toString())
    }

    @Test
    fun test_9() {
        val range = VGSTextRange(3, 3)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range))
            .getTransformation(EXAMPLE, null)

        assertEquals("424•424242424242", charSequence.toString())
    }

    @Test
    fun test_10() {
        val range = VGSTextRange(0, 2)
        val rangeTwo = VGSTextRange(5, 6)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range, rangeTwo))
            .getTransformation(EXAMPLE, null)

        assertEquals("•••24••242424242", charSequence.toString())
    }

    @Test
    fun test_11() {
        val range = VGSTextRange(0, 2)
        val rangeTwo = VGSTextRange(5, -6)
        val charSequence = SecureTransformationMethod(secureSymbol, arrayOf(range, rangeTwo))
            .getTransformation(EXAMPLE, null)

        assertEquals("•••2424242424242", charSequence.toString())
    }

    @Test
    fun test_12() {
        val range = VGSTextRange(0, 2)
        val rangeTwo = VGSTextRange(5, -6)
        val charSequence = SecureTransformationMethod('#', arrayOf(range, rangeTwo))
            .getTransformation(EXAMPLE, null)

        assertEquals("###2424242424242", charSequence.toString())
    }

    @Test
    fun test_13() {
        val range = VGSTextRange(0, 2)
        val rangeTwo = VGSTextRange(5, -6)
        val charSequence = SecureTransformationMethod('#', arrayOf(range, rangeTwo))
            .getTransformation("", null)

        assertEquals("", charSequence.toString())
    }

    @Test
    fun test_14() {
        val range = VGSTextRange(0, 0)
        val rangeTwo = VGSTextRange(0, 0)
        val charSequence = SecureTransformationMethod('#', arrayOf(range, rangeTwo))
            .getTransformation(EXAMPLE, null)

        assertEquals("#242424242424242", charSequence.toString())
    }

    @Test
    fun test_15() {
        val range1 = VGSTextRange(end = 0)
        val charSequence1 = SecureTransformationMethod('#', arrayOf(range1))
            .getTransformation(EXAMPLE, null)

        assertEquals("#242424242424242", charSequence1.toString())

        val range2 = VGSTextRange(2,2)
        val charSequence2 = SecureTransformationMethod('#', arrayOf(range2))
            .getTransformation(EXAMPLE, null)

        assertEquals("42#2424242424242", charSequence2.toString())

        val range3 = VGSTextRange()
        val charSequence3 = SecureTransformationMethod('#', arrayOf(range3))
            .getTransformation(EXAMPLE, null)

        assertEquals("################", charSequence3.toString())
    }

    companion object {
        private const val EXAMPLE = "4242424242424242"
    }
}