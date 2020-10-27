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
    fun isValidEnvironment_correctParams_trueReturned() {
        //Assert
        assertTrue("live".isValidEnvironment())
        assertTrue("live-eu".isValidEnvironment())
        assertTrue("live-eu-3".isValidEnvironment())
        assertTrue("live-eu-3-stage".isValidEnvironment())

        assertTrue("LIVE".isValidEnvironment())
        assertTrue("LIVE-EU".isValidEnvironment())
        assertTrue("LIVE-eu".isValidEnvironment())
        assertTrue("LIVE-EU-3".isValidEnvironment())
        assertTrue("LIVE-eu-3".isValidEnvironment())

        assertTrue("sandbox".isValidEnvironment())
        assertTrue("sandbox-eu".isValidEnvironment())
        assertTrue("sandbox-eu-3".isValidEnvironment())
        assertTrue("sandbox-eu-3-stage".isValidEnvironment())

        assertTrue("SANDBOX".isValidEnvironment())
        assertTrue("SANDBOX-EU".isValidEnvironment())
        assertTrue("SANDBOX-eu".isValidEnvironment())
        assertTrue("SANDBOX-EU-3".isValidEnvironment())
        assertTrue("SANDBOX-eu-3".isValidEnvironment())
    }

    @Test
    fun isValidEnvironment_incorrectParams_falseReturned() {
        //Assert
        assertFalse("live-".isValidEnvironment())
        assertFalse("-live-eu".isValidEnvironment())
        assertFalse("-live-eu-".isValidEnvironment())

        assertFalse("LIVE-".isValidEnvironment())
        assertFalse("-LIVE-EU".isValidEnvironment())
        assertFalse("-LIVE-eu-".isValidEnvironment())

        assertFalse("sandbox-".isValidEnvironment())
        assertFalse("-sandbox-eu".isValidEnvironment())
        assertFalse("-sandbox-eu-".isValidEnvironment())

        assertFalse("SANDBOX-".isValidEnvironment())
        assertFalse("-SANDBOX-EU".isValidEnvironment())
        assertFalse("-SANDBOX-eu-".isValidEnvironment())
    }

    @Test
    fun isUrlValid_correctParams_trueReturned() {
        //Assert
        assertTrue("http://www.exa".isValidUrl())
        assertTrue("https://www.exa".isValidUrl())
        assertTrue("https://www.example.com".isValidUrl())
        assertTrue("https://www.example-example.com".isValidUrl())
        assertTrue("http://www.example.com:8800".isValidUrl())
    }

    @Test
    fun isUrlValid_incorrectParams_falseReturned() {
        //Assert
        assertFalse("www.example".isValidUrl())
        assertFalse("example.com".isValidUrl())
        assertFalse("https://www.exam ple.com".isValidUrl())
    }
}