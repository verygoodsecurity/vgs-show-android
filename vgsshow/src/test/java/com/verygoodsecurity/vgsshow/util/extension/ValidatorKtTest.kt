package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorKtTest {

    @Test
    fun isTenantValid_correctParams_trueReturned() {
        //Assert
        assertTrue("tntaq8uft80".isValidTenantId())
        assertTrue("11111111111".isValidTenantId())
        assertTrue("tnt".isValidTenantId())
        assertTrue("t".isValidTenantId())
    }

    @Test
    fun isTenantValid_incorrectParams_falseReturned() {
        // Arrange
        assertFalse("tntaq8uft80-".isValidTenantId())
        assertFalse("tntaq8uft80$".isValidTenantId())
        assertFalse("tnta%q8uft80".isValidTenantId())
        assertFalse("-".isValidTenantId())
    }

    @Test
    fun isUrlValid_correctParams_trueReturned() {
        //Assert
        assertTrue("www.example".isValidUrl())
        assertTrue("http://www.exa".isValidUrl())
        assertTrue("https://www.exa".isValidUrl())
        assertTrue("https://www.example.com".isValidUrl())
        assertTrue("https://www.example-example.com".isValidUrl())
        assertTrue("http://www.example.com:8800".isValidUrl())
    }

    @Test
    fun isUrlValid_incorrectParams_falseReturned() {
        //Assert
        assertFalse("https://www.exam ple.com".isValidUrl())
    }
}