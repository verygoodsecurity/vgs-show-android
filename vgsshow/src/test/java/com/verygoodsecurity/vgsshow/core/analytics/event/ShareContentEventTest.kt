package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test

class ShareContentEventTest: BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val field = "field"

        val expected = mapOf(
            "type" to "ContentSharing",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
        )

        // Act
        val event = ShareContentEvent(field)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}