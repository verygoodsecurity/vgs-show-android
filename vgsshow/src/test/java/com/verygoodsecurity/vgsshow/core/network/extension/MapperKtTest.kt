package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.data.request.JsonRequestData
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperKtTest {

    @Test
    fun toHttpRequest_successfullyMapped() {
        val url = "URL"
        val path = "path"
        val method = VGSHttpMethod.POST
        val headers = mapOf("header" to "value")
        val extraHeaders = mapOf("extra_header" to "extra_value")
        val body = mapOf("key" to "value")
        val timeoutInterval = 100L

        val request = VGSRequest.Builder(path, method)
            .headers(headers)
            .body(body)
            .requestTimeoutInterval(timeoutInterval)
            .build()

        val result = request.toHttpRequest(url, extraHeaders)

        assertEquals(url, result.url)
        assertEquals(path, result.path)
        assertEquals(method, result.method)
        assertEquals(headers + extraHeaders, result.headers)
        assertEquals(JsonRequestData(body).getRawData(), result.data?.getRawData())
        assertEquals(timeoutInterval, result.requestTimeoutInterval)
    }

    @Test
    fun toContentType_json_successfullyMapped() {
        assertEquals("application/json", VGSHttpBodyFormat.JSON.toContentType())
    }

    @Test
    fun toContentType_form_successfullyMapped() {
        assertEquals("application/x-www-form-urlencoded", VGSHttpBodyFormat.X_WWW_FORM_URLENCODED.toContentType())
    }

    @Test
    fun stringToJsonOrNull_validData_successfullyMapped() {
        val data = "{\"key\":\"value\"}"

        val result = data.toJsonOrNull()

        assertEquals(data, result.toString())
    }

    @Test
    fun stringToJsonOrNul_invalidData_nullReturned() {
        val data = "{\"key\"=\"value\"}"

        val result = data.toJsonOrNull()

        assertNull(result)
    }

    @Test
    fun mapToJsonOrNull_validData_successfullyMapped() {
        val data = mapOf("key" to "value")
        val expected = JSONObject("{\"key\":\"value\"}")

        val result = data.toJsonOrNull()

        assertEquals(expected.toString(), result.toString())
    }
}