package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.ArrayList

class JsonMapperKtTest {

    @Test
    fun mapToJson_simpleKeyValue_successExpected() {
        // Arrange
        val expectedResult = "{\"card_number\":\"1111111111111111\"}"
        val map = mapOf("card_number" to "1111111111111111")
        // Act
        val result = map.toJSON().toString()
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun mapToJson_incorrectSimpleKeyValue_failureExpected() {
        // Arrange
        val expectedResult = "{\"card_number\":\"1111111111111111\"}"
        val map = mapOf("card_number" to 1111111111111111)
        // Act
        val result = map.toJSON().toString()
        // Assert
        assertNotEquals(expectedResult, result)
    }

    @Test
    fun mapToJson_mapInMap_successExpected() {
        // Arrange
        val expectedResult = "{\"card\":{\"number\":\"1111111111111111\"}}"
        val map = mapOf("card" to mapOf("number" to "1111111111111111"))
        // Act
        val result = map.toJSON().toString()
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun mapToJson_mapInMapWithArray_successExpected() {
        // Arrange
        val expectedResult = "{\"card\":{\"number\":[1,1,1,1]}}"
        val map = mapOf("card" to mapOf("number" to arrayOf(1, 1, 1, 1)))
        // Act
        val result = map.toJSON().toString()
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun jsonToMap_simpleKeyValue_successExpected() {
        // Arrange
        val expectedResult = mapOf("response" to mapOf("card_number" to "1111111111111111"))
        val json = "{\"card_number\":\"1111111111111111\"}"
        // Act
        val result = json.toMap()
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun jsonToMap_mapInMap_successExpected() {
        // Arrange
        val expectedResult = mapOf(
            "response" to mapOf("card" to mapOf("number" to "1111111111111111"))
        )
        val json = "{\"card\":{\"number\":\"1111111111111111\"}}"
        // Act
        val result = json.toMap()
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun jsonToMap_mapInMapWithArray_successExpected() {
        // Arrange
        val expectedResult = mapOf(
            "response" to mapOf("card" to mapOf("number" to ArrayList<Int>().apply {
                add(1)
                add(1)
                add(1)
                add(1)
            }))
        )
        val json = "{\"card\":{\"number\":[1,1,1,1]}}"
        // Act
        val result = json.toMap()
        // Assert
        assertEquals(expectedResult, result)
    }
}