package com.verygoodsecurity.vgsshow.core.helper

import com.verygoodsecurity.vgsshow.widget.VGSTextView
import io.mockk.mockk
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ViewsStoreTest {

    private lateinit var sut: ViewsStore

    private val testView = mockk<VGSTextView>(relaxed = true)
    private val testView2 = mockk<VGSTextView>(relaxed = true)

    @Before
    fun setUp() {
        sut = ViewsStore()
    }

    @Test
    fun add_viewAdded() {
        // Act
        sut.add(testView)
        // Assert
        assertThat(sut.getViews(), CoreMatchers.hasItem(testView))
    }

    @Test
    fun add_duplicateViews_oneViewAdded() {
        // Act
        sut.add(testView)
        sut.add(testView)
        // Assert
        assertEquals(sut.getViews().size, 1)
    }

    @Test
    fun remove_viewRemoved() {
        // Act
        sut.add(testView)
        sut.remove(testView)
        // Assert
        assertTrue(sut.getViews().isEmpty())
    }

    @Test
    fun remove_twoViewsInside_correctViewRemoved() {
        // Act
        sut.add(testView)
        sut.add(testView2)
        sut.remove(testView)
        // Assert
        assertThat(sut.getViews(), CoreMatchers.hasItem(testView2))
    }

    @Test
    fun clear_twoViewsInside_allViewRemoved() {
        // Act
        sut.add(testView)
        sut.add(testView2)
        sut.clear()
        // Assert
        assertTrue(sut.getViews().isEmpty())
    }
}