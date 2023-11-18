package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class ResponseEventTest : BaseEventTest() {

    @Test
    fun `createSuccessful - event params correct`() {
        // Arrange
        val code = 200
        val hasText = Random.nextBoolean()
        val hasPDF = Random.nextBoolean()
        val expected = mapOf(
            "type" to "Submit",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "statusCode" to "200",
            "status" to "Ok",
            "content" to mutableListOf<String>().apply {
                if (hasText) add("text")
                if (hasPDF) add("pdf")
            }
        )

        // Act
        val event = ResponseEvent.createSuccessful(
            code = code,
            hasText = hasText,
            hasPDF = hasPDF,
        )

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }

    @Test
    fun `createFailed - event params correct`() {
        // Arrange
        val code = 200
        val hasText = Random.nextBoolean()
        val hasPDF = Random.nextBoolean()
        val message = "message"
        val expected = mapOf(
            "type" to "Submit",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "statusCode" to "200",
            "status" to "Failed",
            "error" to message,
            "content" to mutableListOf<String>().apply {
                if (hasText) add("text")
                if (hasPDF) add("pdf")
            }
        )

        // Act
        val event = ResponseEvent.createFailed(
            code = code,
            hasText = hasText,
            hasPDF = hasPDF,
            message = message
        )

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}