package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test

class UnsubscribeFieldEventTest: BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val field = "field"

        val expected = mapOf(
            "type" to "UnsubscribeField",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
        )

        // Act
        val event = UnsubscribeFieldEvent(field)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}