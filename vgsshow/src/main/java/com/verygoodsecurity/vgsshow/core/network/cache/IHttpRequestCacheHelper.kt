package com.verygoodsecurity.vgsshow.core.network.cache

interface IHttpRequestCacheHelper {

    fun addHeader(key: String, value: String)

    fun removeHeader(key: String)

    fun getHeaders(): Map<String, String>

    fun clearHeaders()

    fun addData(key: String, value: Any)

    fun removeData(key: String)

    fun getData(): Map<String, Any>

    fun clearData()

    fun clearAll()
}