package com.verygoodsecurity.vgsshow.core.helper

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.model.data.response.JsonResponseData
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsImageState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsPdfState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.compose.transformation.VgsVisualTransformation
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StatesStoreTest {

    private lateinit var sut: StatesStore

    // "AAAA" is valid Base64 (3 zero bytes); the content itself is irrelevant, decodeBitmap is mocked.
    private val data = JsonResponseData(
        """{"text": "1111", "image": "AAAA", "pdf": "AAAA"}"""
    )

    @Before
    fun setUp() {
        sut = StatesStore()
        mockkStatic(ByteArray::decodeBitmap)
        every { any<ByteArray>().decodeBitmap() } returns
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    @After
    fun tearDown() {
        unmockkStatic(ByteArray::decodeBitmap)
    }

    private fun text(path: String = "text", ignore: Boolean = false) =
        mutableStateOf(VgsTextState(path, ignore, VgsVisualTransformation.None))

    private fun image(path: String = "image", ignore: Boolean = false) =
        mutableStateOf(VgsImageState(path, ignore))

    private fun pdf(path: String = "pdf", ignore: Boolean = false) =
        mutableStateOf(VgsPdfState(path, ignore))

    @Suppress("UNCHECKED_CAST")
    private fun <T : BaseState> StatesStore.addState(state: MutableState<T>) =
        add(state as MutableState<BaseState>)

    @Test
    fun add_duplicateStates_oneStateAdded() {
        val state = text()
        sut.addState(state)
        assertFalse(sut.addState(state))
        assertEquals(1, sut.getAll().size)
    }

    @Test
    fun update_textState_textRevealed() {
        val state = text()
        sut.addState(state)

        sut.update(data)

        assertEquals("1111", state.value.rawText)
        assertFalse(state.value.isEmpty)
    }

    @Test
    fun update_imageState_imageRevealed() {
        val state = image()
        sut.addState(state)

        sut.update(data)

        assertTrue(state.value.hasImage)
        assertTrue(state.value.isImageValid)
    }

    @Test
    fun update_pdfState_documentRevealed() {
        val state = pdf()
        sut.addState(state)

        sut.update(data)

        assertTrue(state.value.hasDocument)
    }

    @Test
    fun update_ignoredState_notRevealed() {
        val state = text(ignore = true)
        sut.addState(state)

        sut.update(data)

        assertTrue(state.value.isEmpty)
    }

    @Test
    fun update_unknownContentPath_stateUntouched() {
        val state = text(path = "missing")
        sut.addState(state)

        sut.update(data)

        assertTrue(state.value.isEmpty)
    }

    @Test
    fun update_nullData_stateUntouched() {
        val state = text()
        sut.addState(state)

        sut.update(null)

        assertTrue(state.value.isEmpty)
    }

    @Test
    fun update_imageDecoded_onLoadCompleteCalled() {
        var completed = 0
        val state = image()
        state.value.onLoadCompleteCallback = { completed++ }
        sut.addState(state)

        sut.update(data)

        assertEquals(1, completed)
    }

    @Test
    fun update_imageNotDecodable_onLoadErrorCalled() {
        every { any<ByteArray>().decodeBitmap() } throws VGSException.ImageNotValid()
        var error: VGSException? = null
        var completed = false
        val state = image()
        state.value.onLoadCompleteCallback = { completed = true }
        state.value.onLoadErrorCallback = { error = it }
        sut.addState(state)

        sut.update(data)

        assertTrue(error is VGSException.ImageNotValid)
        assertFalse(completed)
        assertTrue(state.value.hasImage)
        assertFalse(state.value.isImageValid)
    }

    @Test
    fun update_callbackThrows_exceptionNotSwallowedAsDecodeFailure() {
        val state = image()
        state.value.onLoadCompleteCallback = { throw IllegalArgumentException("app bug") }
        sut.addState(state)

        val e = assertThrows(IllegalArgumentException::class.java) { sut.update(data) }

        assertEquals("app bug", e.message)
    }

    @Test
    fun update_callbackUnsubscribesAnotherState_noConcurrentModification() {
        val other = text()
        val state = image()
        state.value.onLoadCompleteCallback = { sut.remove(other as MutableState<BaseState>) }
        sut.addState(state)
        sut.addState(other)

        sut.update(data)

        assertTrue(state.value.hasImage)
        assertEquals(1, sut.getAll().size)
    }

    @Test
    fun clear_allStatesRemoved() {
        sut.addState(text())
        sut.addState(pdf())

        sut.clear()

        assertTrue(sut.isEmpty())
    }
}
