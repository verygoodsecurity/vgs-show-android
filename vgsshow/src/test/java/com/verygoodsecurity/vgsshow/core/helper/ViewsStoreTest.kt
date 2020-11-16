package com.verygoodsecurity.vgsshow.core.helper

import com.verygoodsecurity.vgsshow.widget.textview.VGSTextView
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ViewsStoreTest {

    private lateinit var sut: ViewsStore

    private val testView = mockk<VGSTextView>(relaxed = true)
    private val testView2 = mockk<VGSTextView>(relaxed = true)

    @Before
    fun setUp() {
        sut = ViewsStore()
    }

    @Test
    fun update_setTextCalled() {
        // Arrange
        sut.add(testView)
        sut.add(testView2)
        // Act
        sut.update(null)
        // Assert
        verify { testView.setText(null) }
        verify { testView2.setText(null) }
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

    @Test
    fun update_setTextCalled_ignoreField() {
        // Arrange
        val ignoredField = mock(VGSTextView::class.java)
        sut.add(ignoredField)
        // Act
        sut.update(null)
        // Assert
        verify(ignoredField, times(1)).setText(null)

        // Act
        doReturn(true).`when`(ignoredField).ignoreField
        ignoredField.ignoreField = true

        // Assert
        verify(ignoredField, times(1)).setText(null)
    }
}