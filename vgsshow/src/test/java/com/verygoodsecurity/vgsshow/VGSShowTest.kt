package com.verygoodsecurity.vgsshow

import android.content.Context
import android.util.Log
import com.verygoodsecurity.vgsshow.core.listener.VGSResponseListener
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
    private val testListenerOne = mockk<VGSResponseListener>(relaxed = true)
    private val testListenerTwo = mockk<VGSResponseListener>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
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

    companion object {

        private const val DEFAULT_TENANT_ID = ""
        private const val DEFAULT_ENVIRONMENT = ""
    }
}