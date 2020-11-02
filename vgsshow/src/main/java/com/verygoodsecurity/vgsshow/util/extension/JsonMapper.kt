package com.verygoodsecurity.vgsshow.util.extension

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

///**
// * Merge "source" into "target". If fields have equal name, merge them recursively.
// * @return the merged object (target).
// */
//@Throws(JSONException::class)
//fun JSONObject.deepMerge(source: JSONObject): JSONObject {
//    for (key in JSONObject.getNames(source)) {
//        val value = source[key]
//        if (!has(key)) {
//            put(key, value)
//        } else {
//            // existing value for "key" - recursively deep merge:
//            if (value is JSONObject) {
//                getJSONObject(key).deepMerge(value)
//            } else {
//                put(key, value)
//            }
//        }
//    }
//    return this
//}

internal fun Map<*, *>.toJSON(): JSONObject {
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
                val j = (entry.value as Map<*, *>).toJSON()
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
                val obj = entry.toJSON()
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
                val obj = entry.toJSON()
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

@Throws(JSONException::class)
internal fun String.toMap(): Map<String, Any> {
    val resultMap = HashMap<String, Any>()
    when {
        isJSONObjectValid(this) -> {
            val json = JSONObject(this)
            resultMap["response"] = json.toMap()
        }
        isJSONArrayValid(this) -> {
            val json = JSONArray(this)
            resultMap["response"] = json.toList()
        }
    }

    return resultMap
}

private fun isJSONObjectValid(str: String?): Boolean {
    var isObject = false
    try {
        JSONObject(str)
        isObject = true
    } catch (ex: JSONException) {
    } finally {
        return isObject
    }
}


private fun isJSONArrayValid(str: String?): Boolean {
    var isObject = false
    try {
        JSONArray(str)
        isObject = true
    } catch (ex: JSONException) {
    } finally {
        return isObject
    }
}

@Throws(JSONException::class)
private fun JSONObject.toMap(): Map<String, Any> {
    val map: MutableMap<String, Any> =
        HashMap()
    val keysItr = this.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        var value = this[key]
        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = value.toMap()
        }
        map[key] = value
    }
    return map
}

@Throws(JSONException::class)
private fun JSONArray.toList(): List<Any> {
    val list: MutableList<Any> = ArrayList()
    for (i in 0 until this.length()) {
        var value = this[i]
        if (value is JSONArray) {
            value = value.toList()
        } else if (value is JSONObject) {
            value = value.toMap()
        }
        list.add(value)
    }
    return list
}