package com.verygoodsecurity.vgsshow.core.network.cache

internal class HttpRequestCacheHelper : IHttpRequestCacheHelper {

    private val cachedHeaders: MutableMap<String, String> = HashMap()

    private val cachedData: MutableMap<String, Any> = HashMap()

    override fun addHeader(key: String, value: String) {
        cachedHeaders[key] = value
    }

    override fun removeHeader(key: String) {
        cachedHeaders.remove(key)
    }

    override fun getHeaders(): Map<String, String> = cachedHeaders

    override fun clearHeaders() {
        cachedHeaders.clear()
    }

    override fun addData(key: String, value: Any) {
        cachedData[key] = value
    }

    override fun removeData(key: String) {
        cachedData.remove(key)
    }

    override fun getData(): Map<String, Any> = cachedData

    override fun clearData() {
        cachedData.clear()
    }

    override fun clearAll() {
        clearHeaders()
        clearData()
    }
}