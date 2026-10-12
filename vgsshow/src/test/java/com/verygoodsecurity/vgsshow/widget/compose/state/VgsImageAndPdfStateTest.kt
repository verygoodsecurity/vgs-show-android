package com.verygoodsecurity.vgsshow.widget.compose.state

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VgsImageAndPdfStateTest {

    private val bytes = byteArrayOf(1, 2, 3)

    @Before
    fun setUp() {
        mockkStatic(ByteArray::decodeBitmap)
        every { any<ByteArray>().decodeBitmap() } returns
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    @After
    fun tearDown() {
        unmockkStatic(ByteArray::decodeBitmap)
    }

    // Image

    @Test
    fun image_noData_noImage() {
        val state = VgsImageState("image", false)

        assertFalse(state.hasImage)
        assertFalse(state.isImageValid)
    }

    @Test
    fun image_validData_hasValidImage() {
        val state = VgsImageState("image", false, bytes)

        assertTrue(state.hasImage)
        assertTrue(state.isImageValid)
    }

    @Test
    fun image_invalidData_hasImageButNotValid() {
        every { any<ByteArray>().decodeBitmap() } throws VGSException.ImageNotValid()

        val state = VgsImageState("image", false, bytes)

        assertTrue(state.hasImage)
        assertFalse(state.isImageValid)
    }

    @Test
    fun image_copyIgnore_bitmapNotDecodedAgain() {
        val state = VgsImageState("image", false, bytes)

        val copy = state.copy(ignore = true)

        assertTrue(copy.ignore)
        assertSame(state.bitmap, copy.bitmap)
        verify(exactly = 1) { any<ByteArray>().decodeBitmap() }
    }

    @Test
    fun image_copyIgnore_callbacksCarried() {
        var completed = 0
        val state = VgsImageState("image", false, bytes)
        state.onLoadCompleteCallback = { completed++ }

        state.copy(ignore = true).notifyLoadResult()

        assertEquals(1, completed)
    }

    @Test
    fun image_notifyLoadResult_invalidImage_errorReported() {
        every { any<ByteArray>().decodeBitmap() } throws VGSException.ImageNotValid()
        var error: VGSException? = null
        var completed = false
        val state = VgsImageState("image", false, bytes)
        state.onLoadErrorCallback = { error = it }
        state.onLoadCompleteCallback = { completed = true }

        state.notifyLoadResult()

        assertNotNull(error)
        assertFalse(completed)
    }

    @Test
    fun image_notifyLoadResult_noImage_nothingReported() {
        var called = false
        val state = VgsImageState("image", false)
        state.onLoadCompleteCallback = { called = true }
        state.onLoadErrorCallback = { called = true }

        state.notifyLoadResult()

        assertFalse(called)
    }

    @Test
    fun image_equals_comparesContent() {
        assertEquals(VgsImageState("a", false, byteArrayOf(1)), VgsImageState("a", false, byteArrayOf(1)))
        assertFalse(VgsImageState("a", false, byteArrayOf(1)) == VgsImageState("a", false, byteArrayOf(2)))
    }

    @Test
    fun clearImage_noImage() {
        val state = mutableStateOf(VgsImageState("image", false, bytes))

        state.clearImage()

        assertFalse(state.value.hasImage)
        assertFalse(state.value.isImageValid)
    }

    // PDF

    @Test
    fun pdf_noData_noDocument() {
        assertFalse(VgsPdfState("pdf", false).hasDocument)
    }

    @Test
    fun pdf_data_hasDocument() {
        assertTrue(VgsPdfState("pdf", false, bytes).hasDocument)
    }

    @Test
    fun pdf_copy_allCallbacksAndEventsCarried() {
        val calls = mutableListOf<String>()
        val state = VgsPdfState("pdf", false)
        state.onRenderCompleteEvent = { calls.add("completeEvent") }
        state.onRenderErrorEvent = { calls.add("errorEvent") }
        state.onRenderStartCallback = { calls.add("start") }
        state.onRenderCompleteCallback = { calls.add("complete") }
        state.onRenderErrorCallback = { calls.add("error") }
        state.onPageChangedCallback = { _, _ -> calls.add("page") }

        val copy = state.copy(document = bytes).copy(ignore = true)
        copy.onRenderCompleteEvent?.invoke()
        copy.onRenderErrorEvent?.invoke()
        copy.onRenderStartCallback?.invoke(1)
        copy.onRenderCompleteCallback?.invoke(1)
        copy.onRenderErrorCallback?.invoke(RuntimeException())
        copy.onPageChangedCallback?.invoke(0, 1)

        assertEquals(
            listOf("completeEvent", "errorEvent", "start", "complete", "error", "page"),
            calls
        )
    }

    @Test
    fun pdf_equals_comparesContent() {
        assertEquals(VgsPdfState("a", false, byteArrayOf(1)), VgsPdfState("a", false, byteArrayOf(1)))
        assertFalse(VgsPdfState("a", false, byteArrayOf(1)) == VgsPdfState("a", false, byteArrayOf(2)))
    }

    @Test
    fun clearDocument_noDocument() {
        val state = mutableStateOf(VgsPdfState("pdf", false, bytes))

        state.clearDocument()

        assertFalse(state.value.hasDocument)
    }
}
