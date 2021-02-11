package com.verygoodsecurity.vgsshow.core.network.headers

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProxyStaticHeadersStoreTest {

    private lateinit var sut: ProxyStaticHeadersStore

    @Before
    fun setUp() {
        sut = ProxyStaticHeadersStore(true)
    }

    @Test
    fun addHeader_oneHeaderAdded_successHeaderAdded() {
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        //Assert
        assertTrue(sut.getAll().contains(TEST_HEADER_NAME))
        assertEquals(sut.getAll()[TEST_HEADER_NAME], TEST_HEADER_VALUE)
    }

    @Test
    fun addHeader_sameHeaderAdded_successOnlyUniqueKeyStoredAnd() {
        // Arrange
        val testValue = TEST_HEADER_VALUE + 2
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.add(TEST_HEADER_NAME, testValue)
        //Assert
        assertEquals(sut.getAll().size, 2)
    }

    @Test
    fun addHeader_sameHeaderAdded_successCorrectOverriding() {
        // Arrange
        val testValue = TEST_HEADER_VALUE + 2
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.add(TEST_HEADER_NAME, testValue)
        //Assert
        assertEquals(sut.getAll()[TEST_HEADER_NAME], testValue)
    }

    @Test
    fun removeHeader_oneHeaderRemoved_successHeaderRemoved() {
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.remove(TEST_HEADER_NAME)
        //Assert
        assertFalse(sut.getAll().contains(TEST_HEADER_NAME))
    }

    @Test
    fun getHeader_staticHeadersNotAdded_onlyDefaultHeaderReturned() {
        //Assert
        assertEquals(sut.getAll().size, 1)
    }

    @Test
    fun containsUserHeaders_headerAdd_trueReturned() {
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        //Assert
        assertTrue(sut.getCustom().isNotEmpty())
    }

    @Test
    fun clearHeaders() {
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        sut.clear()
        //Assert
        assertEquals(sut.getAll().size, 1)
    }

    @Test
    fun getAll_successDefaultHeadersContains() {
        // Assert
        assertTrue(sut.getAll().containsKey("vgs-client"))
    }

    companion object {

        private const val TEST_HEADER_NAME = "test_header_name"
        private const val TEST_HEADER_VALUE = "test_header_value"
    }
}