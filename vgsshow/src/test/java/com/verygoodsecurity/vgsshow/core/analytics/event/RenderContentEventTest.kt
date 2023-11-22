package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert.assertEquals
import org.junit.Test

class RenderContentEventTest : BaseEventTest() {

    @Test
    fun `createSuccessful - event params correct`() {
        // Arrange
        val field = "field"

        val expected = mapOf(
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
            "type" to "ContentRendering",
            "status" to "Ok",
        )
        // Act
        val event = RenderContentEvent.createSuccessful(field)

        // Assert
        assertEquals(expected, event.attributes)
    }

    @Test
    fun `createFailed - event params correct`() {
        // Arrange
        val field = "field"

        val expected = mapOf(
            "type" to "ContentRendering",
            "status" to "Failed",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field
        )
        // Act
        val event = RenderContentEvent.createFailed(field)

        // Assert
        assertEquals(expected, event.attributes)
    }
}