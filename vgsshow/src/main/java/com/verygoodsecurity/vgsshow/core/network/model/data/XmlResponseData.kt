package com.verygoodsecurity.vgsshow.core.network.model.data

import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import org.w3c.dom.DOMException
import org.w3c.dom.Document

class XmlResponseData constructor(
    private val data: Document
) : ResponseData {

    override fun getValue(key: String): String? {
        return try {
            data.getElementsByTagName(key)?.item(0)?.textContent
        } catch (e: DOMException) {
            null
        }
    }
}