package com.verygoodsecurity.vgsshow.core.network.model.data.request

interface RequestData {

    fun getData(): ByteArray?

    fun isValid(): Boolean
}