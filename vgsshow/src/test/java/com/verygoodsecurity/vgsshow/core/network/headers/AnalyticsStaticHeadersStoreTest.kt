package com.verygoodsecurity.vgsshow.core.network.headers

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AnalyticsStaticHeadersStoreTest {

    private lateinit var sut: AnalyticsStaticHeadersStore

    @Before
    fun setUp() {
        sut = AnalyticsStaticHeadersStore()
    }

    @Test
    fun addHeader_headersNotAdded() {
        // Act
        sut.add(TEST_HEADER_NAME, TEST_HEADER_VALUE)
        //Assert
        assertFalse(sut.getAll().contains(TEST_HEADER_NAME))
        assertNotEquals(sut.getAll()[TEST_HEADER_NAME], TEST_HEADER_VALUE)
    }

    @Test
    fun removeHeader_removeDefaultHeader_defaultHeaderNotRemoved() {
        // Act
        sut.remove("vgs-client")
        //Assert
        assertTrue(sut.getAll().containsKey("vgs-client"))
    }

    @Test
    fun clear_defaultHeaderNotRemoved() {
        // Act
        sut.clear()
        //Assert
        assertTrue(sut.getAll().containsKey("vgs-client"))
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