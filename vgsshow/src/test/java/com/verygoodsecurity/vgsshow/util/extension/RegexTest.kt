package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class RegexTest {

    @Test
    fun successTransformation() {
        val transformedText = "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".transformWithRegex(
            TEXT,
            "\$1 \$2 \$3 \$4"
        )

        assertEquals(TRANSFORMED_EXAMPLE, transformedText)
    }

    @Test
    fun successTransformation_2() {
        val transformedText = "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".transformWithRegex(
            TEXT,
            "$1 $2 $3 $4"
        )

        assertEquals(TRANSFORMED_EXAMPLE, transformedText)
    }

    @Test
    fun failedTransformation_errorRegex() {
        val transformedText = "(\\d{4})(\\d{4})(\\d{4})\\d{4})".transformWithRegex(
            TEXT,
            "\$1 \$2 \$3 \$4"
        )

        assertEquals(TEXT, transformedText)
    }

    @Test
    fun failedTransformation_errorRegex_indexOfBounds() {
        val transformedText = "(\\d{5})(\\d{6})".transformWithRegex(
            TEXT,
            "$1 $2 $3"
        )

        assertEquals(TEXT, transformedText)
    }

    companion object {
        private const val TEXT = "4111111111111111"
        private const val TRANSFORMED_EXAMPLE = "4111 1111 1111 1111"
    }
}