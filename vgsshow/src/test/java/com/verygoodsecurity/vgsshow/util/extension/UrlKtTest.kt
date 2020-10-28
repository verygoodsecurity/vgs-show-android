package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertTrue
import org.junit.Test

class UrlKtTest {

    @Test
    fun with_paramWithSlash_extraSlashNotAdded() {
        // Arrange
        val url = "https://www.example.com"
        val path = "/post"
        // Act
        val result = url with path
        // Assert
        assertTrue(result.count { it.toString() == "/" } == 3)
    }

    @Test
    fun with_paramWithoutSlash_slashAdded() {
        // Arrange
        val url = "https://www.example.com"
        val path = "post"
        // Act
        val result = url with path
        // Assert
        assertTrue(result.count { it.toString() == "/" } == 3)
    }
}