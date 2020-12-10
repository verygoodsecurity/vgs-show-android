package com.verygoodsecurity.vgsshow.core.network.model.data.response

internal interface ResponseData {

    fun getValue(key: String): String?
}