package com.verygoodsecurity.vgsshow.core.network.cache

interface IVGSCustomHeaderStore {

    fun addHeader(key: String, value: String)

    fun removeHeader(key: String)

    fun getHeaders(): Map<String, String>

    fun clearHeaders()
}