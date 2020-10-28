package com.verygoodsecurity.vgsshow.util.url

import android.util.Log
import com.verygoodsecurity.vgsshow.core.Environment
import com.verygoodsecurity.vgsshow.util.extension.isValidUrl
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UrlHelperTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun buildProxyUrl_correctParams_validUrlReturned() {
        // Arrange
        val tenantId = "tntqq8uft90"
        val environment = Environment.SANDBOX
        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment.rawValue)
        // Assert
        assertTrue(result.isValidUrl())
    }

    @Test
    fun buildProxyUrl_incorrectTenant_invalidUrlReturned() {
        // Arrange
        val tenantId = "-4555i88374"
        val environment = Environment.SANDBOX
        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment.rawValue)
        // Assert
        assertFalse(result.isValidUrl())
    }

    @Test
    fun buildProxyUrl_emptyTenant_invalidUrlReturned() {
        // Arrange
        val tenantId = ""
        val environment = Environment.SANDBOX
        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment.rawValue)
        // Assert
        assertFalse(result.isValidUrl())
    }

    @Test
    fun buildProxyUrl_incorrectEnvironment_invalidUrlReturned() {
        // Arrange
        val tenantId = "tntqq8uft90"
        val environment = "sandbox-eu-"
        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment)
        // Assert
        assertFalse(result.isValidUrl())
    }

    @Test
    fun buildProxyUrl_emptyEnvironment_invalidUrlReturned() {
        // Arrange
        val tenantId = "tntqq8uft90"
        val environment = ""
        // Act
        val result = UrlHelper.buildProxyUrl(tenantId, environment)
        // Assert
        assertFalse(result.isValidUrl())
    }
}