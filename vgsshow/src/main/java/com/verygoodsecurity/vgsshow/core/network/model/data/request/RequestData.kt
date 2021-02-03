package com.verygoodsecurity.vgsshow.core.network.model.data.request

internal interface RequestData {

    fun getRawData(): String?

    fun getData(): ByteArray?

    fun isValid(): Boolean
}