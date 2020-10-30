package com.verygoodsecurity.vgsshow.core

import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.isValid
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.isValidEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toEnvironment
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VGSEnvironmentTest {

    @Test
    fun isValid_correctParams_trueReturned() {
        //Assert
        assertTrue(VGSEnvironment.Live().isValid())
        assertTrue(VGSEnvironment.Live("-eu").isValid())
        assertTrue(VGSEnvironment.Live("-eu-3").isValid())
        assertTrue(VGSEnvironment.Live("-eu-3-stage").isValid())

        assertTrue(VGSEnvironment.Live("-EU").isValid())
        assertTrue(VGSEnvironment.Live("-Eu").isValid())

        assertTrue(VGSEnvironment.Sandbox().isValid())
        assertTrue(VGSEnvironment.Sandbox("-eu").isValid())
        assertTrue(VGSEnvironment.Sandbox("-eu-3").isValid())
        assertTrue(VGSEnvironment.Sandbox("-eu-3-stage").isValid())

        assertTrue(VGSEnvironment.Sandbox("-EU").isValid())
        assertTrue(VGSEnvironment.Sandbox("-Eu").isValid())
    }

    @Test
    fun isValid_incorrectParams_falseReturned() {
        //Assert
        assertFalse(VGSEnvironment.Live("-").isValid())
        assertFalse(VGSEnvironment.Live("eu").isValid())
        assertFalse(VGSEnvironment.Live("-eu-").isValid())
        assertFalse(VGSEnvironment.Live("-eu- 3").isValid())

        assertFalse(VGSEnvironment.Sandbox("-").isValid())
        assertFalse(VGSEnvironment.Sandbox("eu").isValid())
        assertFalse(VGSEnvironment.Sandbox("-eu-").isValid())
        assertFalse(VGSEnvironment.Sandbox("-eu- 3").isValid())
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
        assertTrue("LIVE-Eu".isValidEnvironment())

        assertTrue("sandbox".isValidEnvironment())
        assertTrue("sandbox-eu".isValidEnvironment())
        assertTrue("sandbox-eu-3".isValidEnvironment())
        assertTrue("sandbox-eu-3-stage".isValidEnvironment())

        assertTrue("SANDBOX".isValidEnvironment())
        assertTrue("SANDBOX-EU".isValidEnvironment())
        assertTrue("SANDBOX-eu".isValidEnvironment())
        assertTrue("SANDBOX-Eu".isValidEnvironment())
    }

    @Test
    fun isValidEnvironment_incorrectParams_falseReturned() {
        //Assert
        assertFalse("live-".isValidEnvironment())
        assertFalse("-live".isValidEnvironment())
        assertFalse("-live-eu- 3".isValidEnvironment())

        assertFalse("sandbox-".isValidEnvironment())
        assertFalse("-sandbox-eu".isValidEnvironment())
        assertFalse("-sandbox-eu- 3".isValidEnvironment())
    }

    @Test
    fun toEnvironment_correctSandbox_validValueReturned() {
        // Arrange
        val environment = "sandbox"
        // Act
        val result = environment.toEnvironment()
        //Assert
        assertTrue(result is VGSEnvironment.Sandbox)
    }

    @Test
    fun toEnvironment_sandboxWithSuffix_correctSuffixReturned() {
        // Arrange
        val environment = "sandbox-eu"
        // Act
        val result = environment.toEnvironment()
        //Assert
        assertTrue(result.value.contains("-eu"))
    }

    @Test
    fun toEnvironment_correctLive_validValueReturned() {
        // Arrange
        val environment = "live"
        // Act
        val result = environment.toEnvironment()
        //Assert
        assertTrue(result is VGSEnvironment.Live)
    }

    @Test
    fun toEnvironment_liveWithSuffix_correctSuffixReturned() {
        // Arrange
        val environment = "live-eu"
        // Act
        val result = environment.toEnvironment()
        //Assert
        assertTrue(result.value.contains("-eu"))
    }

    @Test
    fun toEnvironment_incorrectEnvironment_emptyEnvironmentReturned() {
        // Arrange
        val environment = "live- eu"
        // Act
        val result = environment.toEnvironment()
        //Assert
        assertTrue(result is VGSEnvironment.Empty)
    }
}