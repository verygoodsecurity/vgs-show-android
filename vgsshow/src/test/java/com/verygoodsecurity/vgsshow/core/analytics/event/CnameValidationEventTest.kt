package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test

class CnameValidationEventTest : BaseEventTest() {

    @Test
    fun `createSuccessful - event params correct`() {
        // Arrange
        val host = "test"
        val latency = 1L

        val expected = mapOf(
            "type" to "HostNameValidation",
            "status" to "OK",
            "hostname" to host,
            "latency" to latency,
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP
        )
        // Act
        val event = CnameValidationEvent.createSuccessful(host, latency)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }

    @Test
    fun `createSuccessful - null host - event params correct`() {
        // Arrange
        val latency = 1L

        val expected = mapOf(
            "type" to "HostNameValidation",
            "status" to "OK",
            "hostname" to "",
            "latency" to latency,
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP
        )
        // Act
        val event = CnameValidationEvent.createSuccessful(null, latency)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }

    @Test
    fun `createFailed - event params correct`() {
        // Arrange
        val host = "test"
        val latency = 1L

        val expected = mapOf(
            "type" to "HostNameValidation",
            "status" to "FAILED",
            "hostname" to host,
            "latency" to latency,
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP
        )
        // Act
        val event = CnameValidationEvent.createFailed(host, latency)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }

    @Test
    fun `createFailed - null host - event params correct`() {
        // Arrange
        val latency = 1L

        val expected = mapOf(
            "type" to "HostNameValidation",
            "status" to "FAILED",
            "hostname" to "",
            "latency" to latency,
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP
        )
        // Act
        val event = CnameValidationEvent.createFailed(null, latency)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}