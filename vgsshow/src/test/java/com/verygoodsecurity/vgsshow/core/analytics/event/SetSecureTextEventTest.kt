package com.verygoodsecurity.vgsshow.core.analytics.event

import org.junit.Assert
import org.junit.Test

class SetSecureTextEventTest: BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val field = "field"
        val contentPath = "contentPath"

        val expected = mapOf(
            "type" to "SetSecureTextRange",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
            "contentPath" to contentPath,
        )

        // Act
        val event = SetSecureTextEvent(field, contentPath)

        // Assert
        Assert.assertEquals(expected, event.attributes)
    }
}