package com.verygoodsecurity.vgsshow.core.network.model.data.request

import com.verygoodsecurity.vgsshow.core.network.extension.toJsonByteArray
import com.verygoodsecurity.vgsshow.util.extension.isValidJson

class JsonRequestData constructor(private val data: Map<String, Any>) : RequestData {

    override fun getData(): ByteArray? = data.toJsonByteArray()

    override fun isValid(): Boolean = data.isValidJson()
}