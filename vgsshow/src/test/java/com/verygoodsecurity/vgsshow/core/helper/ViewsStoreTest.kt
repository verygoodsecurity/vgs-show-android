package com.verygoodsecurity.vgsshow.core.helper

import com.verygoodsecurity.vgsshow.core.network.model.data.response.JsonResponseData
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ViewsStoreTest {

    private lateinit var sut: ViewsStore

    private val testView = mockk<VGSTextView>(relaxed = true)
    private val testView2 = mockk<VGSTextView>(relaxed = true)

    private val testResponseData = JsonResponseData(
        JSONObject(
            mapOf(
                "number" to "1111",
                "date" to "0000",
            )
        )
    )

    @Before
    fun setUp() {
        sut = ViewsStore()
        every { testView.getContentPath() } returns "number"
        every { testView2.getContentPath() } returns "date"
    }

    @Test
    fun update_setTextCalled() {
        // Arrange
        sut.add(testView)
        sut.add(testView2)
        // Act
        sut.update(testResponseData)
        // Assert
        verify { testView.setText("1111") }
        verify { testView2.setText("0000") }
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
    fun update_setTextNotCalled_ignoreField() {
        // Arrange
        every { testView.ignoreField } returns true
        // Act
        sut.update(testResponseData)
        // Assert
        verify(exactly = 0) { testView.setText("1111") }
    }
}