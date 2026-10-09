package com.verygoodsecurity.vgsshow.core.helper

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.core.network.model.data.response.JsonResponseData
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsPdfState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsSecureVisualTransformation
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsVisualTransformation
import com.verygoodsecurity.vgsshow.widget.view.textview.model.VGSTextRange
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CompositeStoreTest {

    private val copied = mutableListOf<Triple<String, String, VGSAnalyticsCopyFormat>>()
    private val secureRanges = mutableListOf<Pair<String, String>>()
    private val renderCompleted = mutableListOf<Pair<String, String>>()
    private val renderFailed = mutableListOf<Pair<String, String>>()

    private lateinit var sut: CompositeStore

    @Before
    fun setUp() {
        sut = CompositeStore(
            onCopy = { type, path, format -> copied.add(Triple(type, path, format)) },
            onSecureTextRangeSet = { type, path -> secureRanges.add(type to path) },
            onRenderComplete = { type, path -> renderCompleted.add(type to path) },
            onRenderError = { type, path -> renderFailed.add(type to path) },
            onDocumentShare = {},
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun MutableState<*>.asBase() = this as MutableState<BaseState>

    private fun text(transformation: VgsVisualTransformation = VgsVisualTransformation.None) =
        mutableStateOf(VgsTextState("number", false, transformation))

    private fun pdf() = mutableStateOf(VgsPdfState("pdf", false))

    @Test
    fun addState_duplicate_addedOnce() {
        val state = text().asBase()

        assertTrue(sut.add(state))
        assertFalse(sut.add(state))
        assertEquals(1, sut.widgetsStore.getAll().size)
    }

    @Test
    fun addState_secureText_secureRangeReported() {
        sut.add(text(VgsSecureVisualTransformation(VGSTextRange(0, 3))).asBase())

        assertEquals(listOf("text" to "number"), secureRanges)
    }

    @Test
    fun addState_nonSecureText_secureRangeNotReported() {
        sut.add(text().asBase())

        assertTrue(secureRanges.isEmpty())
    }

    @Test
    fun addState_text_secureEventReportsLaterTransformationChange() {
        val state = text()
        sut.add(state.asBase())

        val secure = state.value.copy(
            ignore = false,
            visualTransformation = VgsSecureVisualTransformation(VGSTextRange(0, 3)),
        )
        secure.secureTextRangeEvent?.invoke()

        assertEquals(1, secureRanges.size)
    }

    @Test
    fun addState_text_copyEventReported() {
        val state = text()
        sut.add(state.asBase())
        state.value = state.value.copy(text = "1111")

        state.value.copyToClipboardEvent?.invoke(VGSAnalyticsCopyFormat.RAW)

        assertEquals(listOf(Triple("text", "number", VGSAnalyticsCopyFormat.RAW)), copied)
    }

    @Test
    fun addState_pdf_renderEventsReported() {
        val state = pdf()
        sut.add(state.asBase())

        state.value.onRenderCompleteEvent?.invoke()
        state.value.onRenderErrorEvent?.invoke()

        assertEquals(listOf("pdf" to "pdf"), renderCompleted)
        assertEquals(listOf("pdf" to "pdf"), renderFailed)
    }

    @Test
    fun removeState_eventsCleared() {
        val textState = text(VgsSecureVisualTransformation(VGSTextRange(0, 3)))
        val pdfState = pdf()
        sut.add(textState.asBase())
        sut.add(pdfState.asBase())

        assertTrue(sut.remove(textState.asBase()))
        assertTrue(sut.remove(pdfState.asBase()))

        assertNull(textState.value.copyToClipboardEvent)
        assertNull(textState.value.secureTextRangeEvent)
        assertNull(pdfState.value.onRenderCompleteEvent)
        assertNull(pdfState.value.onRenderErrorEvent)
    }

    @Test
    fun removeState_notAdded_false() {
        assertFalse(sut.remove(text().asBase()))
    }

    @Test
    fun update_revealsViewsAndStates() {
        val view = mockk<VGSTextView>(relaxed = true)
        every { view.getContentPath() } returns "number"
        val state = text()
        sut.add(view)
        sut.add(state.asBase())

        sut.update(JsonResponseData("""{"number": "1111"}"""))

        assertEquals("1111", state.value.rawText)
        io.mockk.verify { view.setText("1111") }
    }

    @Test
    fun hasTextAndHasPdf_includeStates() {
        assertFalse(sut.hasText())
        assertFalse(sut.hasPdf())

        sut.add(text().asBase())
        assertTrue(sut.hasText())
        assertFalse(sut.hasPdf())

        sut.add(pdf().asBase())
        assertTrue(sut.hasPdf())
    }

    @Test
    fun clear_removesViewsAndStates() {
        val view = mockk<VGSTextView>(relaxed = true)
        sut.add(view)
        sut.add(text().asBase())

        sut.clear()

        assertTrue(sut.viewsStore.isEmpty())
        assertTrue(sut.widgetsStore.isEmpty())
    }
}
