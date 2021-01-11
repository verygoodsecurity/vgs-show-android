package com.verygoodsecurity.vgsshow.widget.view.textview.model

import junit.framework.Assert.*
import org.junit.Test

class VGSTextRangeTest {

    @Test
    fun isValid_noParams_trueExpected() {
        val range = VGSTextRange()
        assertTrue(range.isValid(4))
    }

    @Test
    fun isValid_correctEnd_trueExpected() {
        val range = VGSTextRange(end = 4)
        assertTrue(range.isValid(4))
    }

    @Test
    fun isValid_zeroStart_trueExpected() {
        val range = VGSTextRange(start = 0)
        assertTrue(range.isValid(4))
    }

    @Test
    fun isValid_minusStart_falseExpected() {
        val range = VGSTextRange(start = -1)
        assertFalse(range.isValid(4))
    }

    @Test
    fun isValid_minusEnd_falseExpected() {
        val range = VGSTextRange(end = -1)
        assertFalse(range.isValid(4))
    }

    @Test
    fun isValid_startGreaterThanEnd_falseExpected() {
        val range = VGSTextRange(5, 4)
        assertFalse(range.isValid(4))
    }

    @Test
    fun isValid_endGreaterThanEnd_trueExpected() {
        val range = VGSTextRange(0, 100)
        assertTrue(range.isValid(4))
    }

    @Test
    fun isValid_equalStartEnd_trueExpected() {
        val range = VGSTextRange(0, 0)
        assertTrue(range.isValid(4))
    }

    @Test
    fun isValid_endGreaterThanLength_endAsLengthExpected() {
        val range = VGSTextRange(0, 100)
        assertEquals(range.end, 100)
    }

    @Test
    fun isValid_startCorrect_startReturned() {
        val range = VGSTextRange(0, 4)
        assertEquals(range.start, 0)
    }
}