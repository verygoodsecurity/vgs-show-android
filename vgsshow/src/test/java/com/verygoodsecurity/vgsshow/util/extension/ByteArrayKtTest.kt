package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import org.junit.Assert.assertThrows
import org.junit.Test

class ByteArrayKtTest {

    @Test
    fun decodeBitmap_arrayEmpty_exceptionThrown() {
        val byteArray = ByteArray(0)

        assertThrows(VGSException.ImageNotValid::class.java) { byteArray.decodeBitmap() }
    }
}