package com.verygoodsecurity.vgsshow

import android.content.Context
import android.os.Looper
import android.util.Log
import com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener
import com.verygoodsecurity.vgsshow.widget.textview.VGSTextView
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VGSShowTest {

    lateinit var sut: VGSShow

    private val context = mockk<Context>(relaxed = true)
    private val looper = mockk<Looper>(relaxed = true)
    private val testListenerOne = mockk<VgsShowResponseListener>(relaxed = true)
    private val testListenerTwo = mockk<VgsShowResponseListener>(relaxed = true)
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
    fun addResponseListener_listenerAdded() {
        // Act
        sut.addResponseListener(testListenerOne)
        // Assert
        assertThat(sut.getResponseListeners(), hasItem(testListenerOne))
    }

    @Test
    fun addResponseListener_addDuplicateListener_oneListenerAdded() {
        // Act
        sut.addResponseListener(testListenerOne)
        sut.addResponseListener(testListenerOne)
        // Assert
        assertTrue(sut.getResponseListeners().size == 1)
    }

    @Test
    fun addResponseListener_listenerRemoved() {
        // Act
        sut.addResponseListener(testListenerOne)
        sut.removeResponseListener(testListenerOne)
        // Assert
        assertFalse(sut.getResponseListeners().contains(testListenerOne))
    }

    @Test
    fun addResponseListener_correctListenerRemoved() {
        // Act
        sut.addResponseListener(testListenerOne)
        sut.addResponseListener(testListenerTwo)
        sut.removeResponseListener(testListenerOne)
        // Assert
        assertFalse(sut.getResponseListeners().contains(testListenerOne))
    }

    @Test
    fun addResponseListener_allListenersRemoved() {
        // Act
        sut.addResponseListener(testListenerOne)
        sut.addResponseListener(testListenerTwo)
        sut.clearResponseListeners()
        // Assert
        assertTrue(sut.getResponseListeners().isEmpty())
    }

    @Test
    fun subscribeView_viewAdded() {
        // Act
        sut.subscribeView(testView)
        // Assert
        assertThat(sut.getViewsStore().getViews(), hasItem(testView))
    }

    @Test
    fun subscribeView_duplicateViews_oneViewAdded() {
        // Act
        sut.subscribeView(testView)
        sut.subscribeView(testView)
        // Assert
        assertEquals(sut.getViewsStore().getViews().size, 1)
    }

    @Test
    fun subscribeView_viewRemoved() {
        // Act
        sut.subscribeView(testView)
        sut.unsubscribeView(testView)
        // Assert
        assertTrue(sut.getViewsStore().getViews().isEmpty())
    }

    @Test
    fun onDestroy_allSourcesCleared() {
        // Arrange
        sut.subscribeView(testView)
        sut.addResponseListener(testListenerOne)
        sut.getStaticHeadersStore().add("test", "test")
        // Act
        sut.onDestroy()
        // Assert
        assertTrue(sut.getResponseListeners().isEmpty())
        assertTrue(sut.getViewsStore().isEmpty())
        assertFalse(sut.getStaticHeadersStore().containsUserHeaders())
    }

    companion object {

        private const val DEFAULT_TENANT_ID = ""
        private const val DEFAULT_ENVIRONMENT = ""
    }
}