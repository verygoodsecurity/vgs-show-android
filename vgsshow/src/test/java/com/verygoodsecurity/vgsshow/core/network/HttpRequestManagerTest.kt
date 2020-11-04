package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.cache.CustomHeaderStore
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.JsonResponseData
import com.verygoodsecurity.vgsshow.util.connection.ConnectionHelper
import io.mockk.mockk
import org.json.JSONException
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.InterruptedIOException
import java.net.MalformedURLException
import java.util.concurrent.TimeoutException

class HttpRequestManagerTest {

    private lateinit var sut: HttpRequestManager

    private val headersStore = mockk<CustomHeaderStore>(relaxed = true)
    private val connectionHelper = mockk<ConnectionHelper>(relaxed = true)

    @Before
    fun setUp() {
        sut = HttpRequestManager("", headersStore, connectionHelper)
    }

    @Test
    fun parseResponse_successResponseWithValidData_successReturned() {
        // Arrange
        val testResponse = HttpResponse(200, true, null, "{test:\"test\"}")
        // Act
        val result = sut.parseResponse(testResponse, VGSHttpBodyFormat.JSON)
        //Assert
        assertTrue(result is VGSResponse.Success)
    }

    @Test
    fun parseResponse_errorResponse_errorVgsResponseReturned() {
        // Arrange
        val testResponse = HttpResponse(-1, false, null, null)
        // Act
        val result = sut.parseResponse(testResponse, VGSHttpBodyFormat.JSON)
        //Assert
        assertTrue(result is VGSResponse.Error)
    }

    @Test
    fun parseResponse_successResponseWithInvalidData_correctErrorTrowed() {
        // Arrange
        val testResponse = HttpResponse(200, true, null, "{test:\"test\"}")
        //Assert
        try {
            sut.parseResponse(testResponse, VGSHttpBodyFormat.JSON)
        } catch (e: Exception) {
            assertTrue(e is VGSException.JSONException)
        }
    }

    @Test
    fun parseResponseData_correctParams_successReturned() {
        // Arrange
        val data = "{test:\"test\"}"
        // Act
        val result = sut.parseResponseData(data, VGSHttpBodyFormat.JSON)
        //Assert
        assertTrue(result is JsonResponseData)
    }

    @Test
    fun parseException_exceptionSuccessfullyParsed() {
        // Arrange
        val exception = Exception()
        // Act
        val result = sut.parseException(exception)
        //Assert
        assertTrue(result is VGSResponse.Error)
    }

    @Test
    fun parseException_malformedURLException_correctExceptionParsed() {
        // Arrange
        val exception = MalformedURLException()
        // Act
        val result = sut.parseException(exception)
        //Assert
        assertTrue((result as VGSResponse.Error).exception is VGSException.UrlNotValid)
    }

    @Test
    fun parseException_timeoutException_correctExceptionParsed() {
        // Arrange
        val exception = TimeoutException()
        // Act
        val result = sut.parseException(exception)
        //Assert
        assertTrue((result as VGSResponse.Error).exception is VGSException.RequestTimeout)
    }

    @Test
    fun parseException_interruptedIOException_correctExceptionParsed() {
        // Arrange
        val exception = InterruptedIOException()
        // Act
        val result = sut.parseException(exception)
        //Assert
        assertTrue((result as VGSResponse.Error).exception is VGSException.RequestTimeout)
    }

    @Test
    fun parseException_JSONException_correctExceptionParsed() {
        // Arrange
        val exception = JSONException("")
        // Act
        val result = sut.parseException(exception)
        //Assert
        assertTrue((result as VGSResponse.Error).exception is VGSException.JSONException)
    }
}