package com.verygoodsecurity.vgsshow.core.analytics.event

import com.verygoodsecurity.vgsshow.widget.VGSTextView
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class CopyToClipboardEventTest : BaseEventTest() {

    @Test
    fun `init - event params correct`() {
        // Arrange
        val field = "field"
        val copyFormat = VGSTextView.CopyTextFormat.FORMATTED
        val status = "status"

        val expected = mapOf(
            "type" to "Copy to clipboard click",
            "localTimestamp" to EVENT_LOCAL_TIMESTAMP,
            "field" to field,
            "copy_format" to copyFormat.name.lowercase(Locale.US),
            "status" to status
        )

        // Act
        val event = CopyToClipboardEvent(field, copyFormat, status)

        // Assert
        assertEquals(expected, event.attributes)
    }
}