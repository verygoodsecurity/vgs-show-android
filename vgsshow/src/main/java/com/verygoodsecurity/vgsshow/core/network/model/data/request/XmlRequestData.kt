package com.verygoodsecurity.vgsshow.core.network.model.data.request

import com.verygoodsecurity.vgsshow.core.network.extension.toDocumentOrNull
import org.w3c.dom.Document

class XmlRequestData(data: String) : RequestData {

    private val data: Document? = data.toDocumentOrNull()

    override fun getData(): ByteArray? = data?.toString()?.toByteArray(Charsets.UTF_8)

    override fun isValid(): Boolean = data != null
}