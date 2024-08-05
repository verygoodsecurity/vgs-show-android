package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class MapKtTest {

    @Test
    fun plus_successfullyAdded() {
        val testMap1 = mapOf(0 to "test0", 1 to "test1")
        val testMap2 = mapOf(2 to "test2", 3 to "test3")
        val expected = mapOf(0 to "test0", 1 to "test1", 2 to "test2", 3 to "test3")

        val result = testMap1 + testMap2

        assertEquals(expected, result)
    }

    @Test
    fun plusOnNull_successfullyAdded() {
        val testMap1 = null
        val testMap2 = mapOf(2 to "test2", 3 to "test3")
        val expected = mapOf(2 to "test2", 3 to "test3")

        val result = testMap1 + testMap2

        assertEquals(expected, result)
    }

    @Test
    fun plusNull_successfullyAdded() {
        val testMap1 = mapOf(0 to "test0", 1 to "test1")
        val testMap2 = null
        val expected = mapOf(0 to "test0", 1 to "test1")

        val result = testMap1 + testMap2

        assertEquals(expected, result)
    }

    @Test
    fun plusItem_successfullyAdded() {
        val map = mapOf(0 to "test0", 1 to "test1")
        val item = 2 to "test2"
        val expected = mapOf(0 to "test0", 1 to "test1", 2 to "test2")

        val result = map.plusItem(item)

        assertEquals(expected, result)
    }

    @Test
    fun plusItemOnNull_successfullyAdded() {
        val map = null
        val item = 2 to "test2"
        val expected = mapOf(2 to "test2")

        val result = map.plusItem(item)

        assertEquals(expected, result)
    }
}