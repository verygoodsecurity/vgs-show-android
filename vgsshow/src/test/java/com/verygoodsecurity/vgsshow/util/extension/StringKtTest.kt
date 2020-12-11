package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringKtTest {

    @Test
    fun concatWithSlash_emptyParam_paramNotAddedAndSlashTo() {
        // Arrange
        val url = "https://www.example.com"
        val path = ""
        // Act
        val result = url concatWithSlash path
        // Assert
        assertEquals(result, "https://www.example.com")
    }

    @Test
    fun concatWithSlash_paramWithSlash_extraSlashNotAdded() {
        // Arrange
        val url = "https://www.example.com"
        val path = "/post"
        // Act
        val result = url concatWithSlash path
        // Assert
        assertEquals(result, "https://www.example.com/post")
    }

    @Test
    fun concatWithSlash_paramWithoutSlash_slashAdded() {
        // Arrange
        val url = "https://www.example.com"
        val path = "post"
        // Act
        val result = url concatWithSlash path
        // Assert
        assertEquals(result, "https://www.example.com/post")
    }

    @Test
    fun concatWithDash_emptyParam_paramNotAddedAndDashTo() {
        // Arrange
        val url = "sandbox"
        val path = ""
        // Act
        val result = url concatWithDash path
        // Assert
        assertEquals(result, "sandbox")
    }

    @Test
    fun concatWithDash_paramWithDash_extraSlashNotAdded() {
        // Arrange
        val url = "sandbox"
        val path = "ua"
        // Act
        val result = url concatWithDash path
        // Assert
        assertEquals(result, "sandbox-ua")
    }

    @Test
    fun concatWithDash_paramWithoutDash_dashAdded() {
        // Arrange
        val url = "sandbox"
        val path = "-ua"
        // Act
        val result = url concatWithDash path
        // Assert
        assertEquals(result, "sandbox-ua")
    }
}