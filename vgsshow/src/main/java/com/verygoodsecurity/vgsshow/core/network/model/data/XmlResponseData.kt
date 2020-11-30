package com.verygoodsecurity.vgsshow.core.network.model.data

import org.w3c.dom.DOMException
import org.w3c.dom.Document

class XmlResponseData constructor(
    private val data: Document
) : IResponseData {

    override fun getValue(key: String): String {
        return try {
            data.getElementsByTagName(key)?.item(0)?.textContent ?: ""
        } catch (e: DOMException) {
            ""
        }
    }
}