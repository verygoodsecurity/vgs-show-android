package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class RequestEventTest : BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val hasCustomData = Random.nextBoolean()
        val hasCustomHeaders = Random.nextBoolean()
        val hasCustomHostname = Random.nextBoolean()
        val hasText = Random.nextBoolean()
        val hasPDF = Random.nextBoolean()
        val expected = mapOf(
            "type" to "BeforeSubmit",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "statusCode" to "200",
            "status" to "Ok",
            "content" to mutableListOf<String>().apply {
                if (hasCustomData) add("custom_data")
                if (hasCustomHeaders) add("custom_header")
                if (hasCustomHostname) add("custom_hostname")
                if (hasText) add("text")
                if (hasPDF) add("pdf")
            }
        )

        // Act
        val event = RequestEvent.createSuccessful(
            hasCustomData = hasCustomData,
            hasCustomHeaders = hasCustomHeaders,
            hasCustomHostname = hasCustomHostname,
            hasText = hasText,
            hasPDF = hasPDF,
        )

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}