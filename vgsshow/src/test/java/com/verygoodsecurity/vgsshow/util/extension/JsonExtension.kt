package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class JsonExtension {

    @Test
    fun retrieveValue_Successful() {
        val json = generateJson()
        val fieldName = "card.data.number"

        val value = json.getValue(fieldName)

        Assert.assertEquals("4111111111111111", value)
    }

    @Test
    fun retrieveValue_Failed_1() {
        val json = generateJson()
        val fieldName = "json.card.data.number"

        val value = json.getValue(fieldName)

        Assert.assertEquals("", value)
    }

    @Test
    fun retrieveValue_Failed_2() {
        val json = generateJson()
        val fieldName = "card.json.data.number"

        val value = json.getValue(fieldName)

        Assert.assertEquals("", value)
    }

    @Test
    fun retrieveValue_Failed_3() {
        val json = generateJson()
        val fieldName = "card.data.number.json"

        val value = json.getValue(fieldName)

        Assert.assertEquals("", value)
    }

    @Test
    fun retrieveValue_Failed_4() {
        val json = generateJson()
        val fieldName = "card.number"

        val value = json.getValue(fieldName)

        Assert.assertEquals("", value)
    }

    private fun generateJson():JSONObject {
        return JSONObject("{\"card\":{\"data\":{\"number\":\"4111111111111111\"}}}")
    }
}