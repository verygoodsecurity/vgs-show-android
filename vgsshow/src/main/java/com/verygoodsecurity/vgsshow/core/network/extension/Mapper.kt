package com.verygoodsecurity.vgsshow.core.network.extension

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.plus
import okhttp3.Response
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

internal fun VGSRequest.toHttpRequest(extraHeaders: Map<String, String>?) = HttpRequest(
    this.path,
    this.method,
    this.headers + extraHeaders,
    this.payload,
    this.requestFormat
)

private const val APPLICATION_JSON = "application/json"
private const val APPLICATION_XML = "application/xml"

internal fun VGSHttpBodyFormat.toContentType() = when (this) {
    VGSHttpBodyFormat.JSON -> APPLICATION_JSON
    VGSHttpBodyFormat.XML -> APPLICATION_XML
}

internal fun Response.toHttpResponse() = HttpResponse(
    this.code,
    this.isSuccessful,
    this.message,
    this.body?.string()
)

internal fun VGSException.toVGSResponse() = VGSResponse.Error.create(this)

@Throws(DOMException::class)
fun String.toDocument(): Document {
    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    return try {
        documentBuilder.parse(InputSource(StringReader(this)))
    } catch (e: Exception) {
        throw DOMException(-1, "")
    }
}