package com.verygoodsecurity.vgsshow.util.url

import android.util.Log
import com.verygoodsecurity.vgsshow.Constants
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.VGSEnvironment.Companion.toVGSEnvironment
import com.verygoodsecurity.vgsshow.util.extension.isValidUrl
import com.verygoodsecurity.vgsshow.util.extension.toURL
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UrlHelperTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @Test
    fun `buildLocalhostUrl - valid port added`() {
        // Arrange
        val localhost = "10.0.2.2"
        val port = 8080

        // Act
        val result = UrlHelper.buildLocalhostUrl(localhost, port)

        // Assert
        assertEquals(port, result.toURL().port)
    }

    @Test
    fun `buildLocalhostUrl - invalid port - empty port returned`() {
        // Arrange
        val localhost = "10.0.2.2"
        val port = 0

        // Act
        val result = UrlHelper.buildLocalhostUrl(localhost, port)

        // Assert
        assertEquals(-1, result.toURL().port)
    }

    @Test
    fun `buildProxyUrl - valid url returned`() {
        // Arrange
        val environment = VGSEnvironment.Sandbox()

        // Act
        val result = UrlHelper.buildProxyUrl(Constants.TENANT, environment)

        // Assert
        assertTrue(result.isValidUrl())
    }

    @Test
    fun `buildProxyUrl - invalid tenant - empty returned`() {
        // Arrange
        val tenantId = "-4555i88374"
        val environment = VGSEnvironment.Sandbox()

        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment)

        // Assert
        assertFalse(result.isValidUrl())
    }

    @Test
    fun `buildProxyUrl - invalid environment - empty returned`() {
        // Arrange
        val environment = "sandbox-eu-".toVGSEnvironment()

        // Act
        val result = UrlHelper.buildProxyUrl(Constants.TENANT, environment)

        // Assert
        assertFalse(result.isValidUrl())
    }

    @Test
    fun `buildProxyUrl_ - emptyEnvironment -  empty returned`() {
        // Arrange
        val environment = VGSEnvironment.Empty

        // Act
        val result = UrlHelper.buildProxyUrl(Constants.TENANT, environment)

        // Assert
        assertFalse(result.isValidUrl())
    }
}