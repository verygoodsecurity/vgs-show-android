package com.verygoodsecurity.vgsshow.core.exception

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import org.junit.Assert.assertEquals
import org.junit.Test

class VGSExceptionTest {

    @Test
    fun custom_exceptionCodeAndMessageCorrect() {
        val code = 1000
        val errorMessage = "Test error message"

        val exception = VGSException.Custom(code = code, errorMessage = errorMessage)

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun urlNotValid_exceptionCodeAndMessageCorrect() {
        val code = 1480
        val errorMessage = "URL Error: Not valid organization parameters"

        val exception = VGSException.UrlNotValid()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun noInternetPermission_exceptionCodeAndMessageCorrect() {
        val code = 1481
        val errorMessage = "Permission denied (missing INTERNET permission?)"

        val exception = VGSException.NoInternetPermission()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun noInternetConnection_exceptionCodeAndMessageCorrect() {
        val code = 1482
        val errorMessage = "No Internet connection"

        val exception = VGSException.NoInternetConnection()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }


    @Test
    fun requestTimeout_exceptionCodeAndMessageCorrect() {
        val code = 1483
        val errorMessage = "TimeoutException"

        val exception = VGSException.RequestTimeout()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }


    @Test
    fun responsePayload_exceptionCodeAndMessageCorrect() {
        val code = 1401
        val errorMessage = "Unexpected Response Data Format"

        val exception = VGSException.ResponsePayload()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun requestPayload_exceptionCodeAndMessageCorrect() {
        val code = 1404
        val bodyFormat = VGSHttpBodyFormat.JSON
        val errorMessage = "Payload is not valid  ${bodyFormat.name}"

        val exception = VGSException.RequestPayload(bodyFormat)

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun imageNotValid_exceptionCodeAndMessageCorrect() {
        val code = 1407
        val errorMessage = "Base64 data is not valid image"

        val exception = VGSException.ImageNotValid()

        assertEquals(code, exception.code)
        assertEquals(errorMessage, exception.message)
    }
}