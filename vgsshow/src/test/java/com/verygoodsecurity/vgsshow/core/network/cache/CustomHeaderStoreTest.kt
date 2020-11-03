package com.verygoodsecurity.vgsshow.core.network.cache

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CustomHeaderStoreTest {

    private lateinit var sut: CustomHeaderStore

    @Before
    fun setUp() {
        sut = CustomHeaderStore()
    }

    @Test
    fun addHeader_oneHeaderAdded_successHeaderAdded() {
        // Act
        sut.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        //Assert
        assertTrue(sut.getHeaders().contains(TEST_HEADER_NAME))
        assertEquals(sut.getHeaders()[TEST_HEADER_NAME], TEST_HEADER_VALUE)
    }

    @Test
    fun addHeader_sameHeaderAdded_successOnlyUniqueKeyStoredAnd() {
        // Arrange
        val testValue = TEST_HEADER_VALUE + 2
        // Act
        sut.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.addHeader(TEST_HEADER_NAME, testValue)
        //Assert
        assertEquals(sut.getHeaders().size, 1)
    }

    @Test
    fun addHeader_sameHeaderAdded_successCorrectOverriding() {
        // Arrange
        val testValue = TEST_HEADER_VALUE + 2
        // Act
        sut.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.addHeader(TEST_HEADER_NAME, testValue)
        //Assert
        assertEquals(sut.getHeaders()[TEST_HEADER_NAME], testValue)
    }

    @Test
    fun removeHeader_oneHeaderRemoved_successHeaderRemoved() {
        // Act
        sut.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.removeHeader(TEST_HEADER_NAME)
        //Assert
        assertFalse(sut.getHeaders().contains(TEST_HEADER_NAME))
    }

    @Test
    fun getHeader_successEmptyMapReturned() {
        //Assert
        assertTrue(sut.getHeaders().isEmpty())
    }

    @Test
    fun clearHeaders() {
        // Act
        sut.addHeader(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.clearHeaders()
        //Assert
        assertTrue(sut.getHeaders().isEmpty())
    }

    companion object {

        private const val TEST_HEADER_NAME = "test_header_name"
        private const val TEST_HEADER_VALUE = "test_header_value"
    }
}