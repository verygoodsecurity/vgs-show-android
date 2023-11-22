package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert.assertEquals
import org.junit.Test

class InitEventTest: BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val field = "field"
        val contentPath = "contentPath"

        val expected = mapOf(
            "type" to "Init",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
            "contentPath" to contentPath,
        )

        // Act
        val event = InitEvent(field, contentPath)

        // Assert
        assertEquals(expected, event.attributes)
    }
}