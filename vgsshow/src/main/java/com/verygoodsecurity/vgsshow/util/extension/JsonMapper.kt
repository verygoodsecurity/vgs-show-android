package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONArray
import org.json.JSONObject

internal fun Map<*, *>.mapToJSON(): JSONObject {
    val jObjectData = JSONObject()
    this.forEach { entry ->
        val key: String = entry.key.toString()
        when (entry.value) {
            is String -> jObjectData.put(key, entry.value)
            is Int -> jObjectData.put(key, entry.value as Int)
            is Long -> jObjectData.put(key, entry.value as Long)
            is Char -> jObjectData.put(key, entry.value as Char)
            is Float -> jObjectData.put(key, entry.value)
            is Double -> jObjectData.put(key, entry.value as Double)
            is Map<*, *> -> {
                val j = (entry.value as Map<*, *>).mapToJSON()
                jObjectData.put(key, j)
            }
            is Array<*> -> {
                val array = (entry.value as Array<*>).mapArrToJSON()
                jObjectData.put(key, array)
            }
            is Collection<*> -> {
                val array = (entry.value as Collection<*>).mapCollectionToJSON()
                jObjectData.put(key, array)
            }
        }
    }
    return jObjectData
}

private fun Collection<*>.mapCollectionToJSON(): JSONArray {
    val array = JSONArray()
    this.forEach { entry ->
        when (entry) {
            is String -> array.put(entry)
            is Int -> array.put(entry)
            is Char -> array.put(entry)
            is Long -> array.put(entry)
            is Float -> array.put(entry)
            is Double -> array.put(entry)
            is Map<*, *> -> {
                val obj = entry.mapToJSON()
                array.put(obj)
            }
            is Array<*> -> {
                val array2 = entry.mapArrToJSON()
                array.put(array2)
            }
            is Collection<*> -> {
                val array2 = entry.mapCollectionToJSON()
                array.put(array2)
            }
        }
    }
    return array
}

private fun Array<*>.mapArrToJSON(): JSONArray {
    val array = JSONArray()
    this.forEach { entry ->
        when (entry) {
            is String -> array.put(entry)
            is Char -> array.put(entry)
            is Int -> array.put(entry)
            is Long -> array.put(entry)
            is Float -> array.put(entry)
            is Double -> array.put(entry)
            is Map<*, *> -> {
                val obj = entry.mapToJSON()
                array.put(obj)
            }
            is Array<*> -> {
                val array2 = entry.mapArrToJSON()
                array.put(array2)
            }
            is Collection<*> -> {
                val array2 = entry.mapCollectionToJSON()
                array.put(array2)
            }
        }
    }
    return array
}