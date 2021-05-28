package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Looper
import android.util.Log
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VGSShowTest {

    private lateinit var sut: VGSShow

    private val context = mockk<Context>(relaxed = true)
    private val looper = mockk<Looper>(relaxed = true)
    private val testListenerOne = mockk<VGSOnResponseListener>(relaxed = true)
    private val testListenerTwo = mockk<VGSOnResponseListener>(relaxed = true)
    private val testView = mockk<VGSTextView>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        mockkStatic(Looper::class)
        every { Log.d(any(), any()) } returns 0
        every { Looper.getMainLooper() } returns looper
        sut = VGSShow(context, DEFAULT_TENANT_ID, DEFAULT_ENVIRONMENT)
    }

    @Test
    fun addOnResponseListener_listenerAdded() {
        // Act
        sut.addOnResponseListener(testListenerOne)
        // Assert
        assertTrue(sut.getResponseListeners().contains(testListenerOne))
    }

    @Test
    fun addOnResponseListener_addDuplicateListener_oneListenerAdded() {
        // Act
        sut.addOnResponseListener(testListenerOne)
        sut.addOnResponseListener(testListenerOne)
        // Assert
        assertTrue(sut.getResponseListeners().size == 1)
    }

    @Test
    fun addOnResponseListener_listenerRemoved() {
        // Act
        sut.addOnResponseListener(testListenerOne)
        sut.removeOnResponseListener(testListenerOne)
        // Assert
        assertFalse(sut.getResponseListeners().contains(testListenerOne))
    }

    @Test
    fun addOnResponseListener_correctListenerRemoved() {
        // Act
        sut.addOnResponseListener(testListenerOne)
        sut.addOnResponseListener(testListenerTwo)
        sut.removeOnResponseListener(testListenerOne)
        // Assert
        assertFalse(sut.getResponseListeners().contains(testListenerOne))
    }

    @Test
    fun addOnResponseListener_allListenersRemoved() {
        // Act
        sut.addOnResponseListener(testListenerOne)
        sut.addOnResponseListener(testListenerTwo)
        sut.clearResponseListeners()
        // Assert
        assertTrue(sut.getResponseListeners().isEmpty())
    }

    @Test
    fun subscribe_viewAdded() {
        // Act
        sut.subscribe(testView)
        // Assert
        assertTrue(sut.getViewsStore().getViews().contains(testView))
    }

    @Test
    fun subscribe_duplicateViews_oneViewAdded() {
        // Act
        sut.subscribe(testView)
        sut.subscribe(testView)
        // Assert
        assertEquals(sut.getViewsStore().getViews().size, 1)
    }

    @Test
    fun subscribe_viewRemoved() {
        // Act
        sut.subscribe(testView)
        sut.unsubscribe(testView)
        // Assert
        assertTrue(sut.getViewsStore().getViews().isEmpty())
    }

    @Test
    fun onDestroy_allSourcesCleared() {
        // Arrange
        sut.subscribe(testView)
        sut.addOnResponseListener(testListenerOne)
        sut.setCustomHeader("test", "test")
        // Act
        sut.onDestroy()
        // Assert
        assertTrue(sut.getResponseListeners().isEmpty())
        assertTrue(sut.getViewsStore().isEmpty())
        assertFalse(sut.headersStore.getCustom().isNotEmpty())
    }

    companion object {

        private const val DEFAULT_TENANT_ID = ""
        private const val DEFAULT_ENVIRONMENT = ""
    }
}