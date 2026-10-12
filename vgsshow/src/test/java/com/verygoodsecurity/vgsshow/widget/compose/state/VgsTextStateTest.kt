package com.verygoodsecurity.vgsshow.widget.compose.state

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import org.robolectric.RuntimeEnvironment
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsChainedVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsRegexVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsSecureVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsVisualTransformation
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VgsTextStateTest {

    private val context = RuntimeEnvironment.getApplication() as Context

    // "4111111111111111" -> "4111 1111 1111 1111" -> first 11 chars masked.
    private val formatThenMask = VgsChainedVisualTransformation(
        listOf(
            VgsRegexVisualTransformation(Regex("(\\d{4})(?=\\d)"), "$1 "),
            VgsSecureVisualTransformation(VGSTextRange(0, 10), '*'),
        )
    )

    private fun revealed(transformation: VgsVisualTransformation = formatThenMask) =
        VgsTextState("number", false, transformation).copy(text = "4111111111111111")

    private fun clipboardText(): String? =
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .primaryClip?.getItemAt(0)?.text?.toString()

    @Test
    fun displayText_formattedThenMasked() {
        // Range is inclusive: the first 11 characters of "4111 1111 1111 1111" are masked.
        assertEquals("***********111 1111", revealed().displayText.text)
    }

    @Test
    fun isEmpty_noText_true() {
        assertTrue(VgsTextState("number", false, VgsVisualTransformation.None).isEmpty)
    }

    @Test
    fun isEmpty_whitespaceText_false() {
        val state = VgsTextState("number", false, VgsVisualTransformation.None).copy(text = " ")
        assertFalse(state.isEmpty)
    }

    @Test
    fun isSecure_chainContainsSecure_true() {
        assertTrue(revealed().isSecure)
        assertFalse(revealed(VgsVisualTransformation.None).isSecure)
    }

    @Test
    fun copyRawToClipboard_rawValueCopied() {
        revealed().copyRawToClipboard(context)

        assertEquals("4111111111111111", clipboardText())
    }

    @Test
    fun copyFormattedToClipboard_formattedAndNotMasked() {
        revealed().copyFormattedToClipboard(context)

        assertEquals("4111 1111 1111 1111", clipboardText())
    }

    @Test
    fun copyFormattedToClipboard_noTransformation_equalsRaw() {
        revealed(VgsVisualTransformation.None).copyFormattedToClipboard(context)

        assertEquals("4111111111111111", clipboardText())
    }

    @Test
    fun copyToClipboard_emptyText_nothingCopiedNoEvent() {
        var events = 0
        val state = VgsTextState("number", false, formatThenMask)
        state.copyToClipboardEvent = { events++ }

        state.copyRawToClipboard(context)
        state.copyFormattedToClipboard(context)

        assertEquals(0, events)
    }

    @Test
    fun copyToClipboard_eventsReportedWithFormat() {
        val formats = mutableListOf<VGSAnalyticsCopyFormat>()
        val state = revealed()
        state.copyToClipboardEvent = { formats.add(it) }

        state.copyRawToClipboard(context)
        state.copyFormattedToClipboard(context)

        assertEquals(listOf(VGSAnalyticsCopyFormat.RAW, VGSAnalyticsCopyFormat.FORMATTED), formats)
    }

    @Test
    fun copy_events_carriedToCopies() {
        var copied = 0
        var secure = 0
        val state = VgsTextState("number", false, formatThenMask)
        state.copyToClipboardEvent = { copied++ }
        state.secureTextRangeEvent = { secure++ }

        val next = state.copy(text = "4111111111111111").copy(ignore = true, visualTransformation = formatThenMask)
        next.copyRawToClipboard(context)
        next.secureTextRangeEvent?.invoke()

        assertEquals(1, copied)
        assertEquals(1, secure)
    }

    @Test
    fun clearText_stateEmpty() {
        val state = mutableStateOf(revealed())

        state.clearText()

        assertTrue(state.value.isEmpty)
    }

    @Test
    fun hasConfig_changedIgnoreOrTransformation_false() {
        val state = revealed()

        assertTrue(state.hasConfig(false, formatThenMask))
        assertFalse(state.hasConfig(true, formatThenMask))
        assertFalse(state.hasConfig(false, VgsVisualTransformation.None))
    }
}
