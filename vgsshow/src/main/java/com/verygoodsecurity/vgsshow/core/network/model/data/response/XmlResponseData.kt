package com.verygoodsecurity.vgsshow.core.network.model.data.response

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import kotlin.system.measureTimeMillis

internal class XmlResponseData constructor(private val data: String = TEST_DATA) : ResponseData {

    override fun getValue(key: String): String? {
        var result: String?
        val executionTime = measureTimeMillis {
            result = read(key)
        }
        Log.d(
            "XmlResponseData",
            "read() function execution time in millis = $executionTime"
        )
        return result
    }

    private fun read(key: String): String? {
        return try {
            var result: String? = null
            val parser = createParser()
            logParser(parser)
            key.split(".").forEachIndexed { index, keyPart ->
                log("Searching for key = $keyPart, index = $index")
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    logParser(parser)
                    if (isKeyFound(parser, keyPart, index)) {
                        parser.next()
                        result = parser.text
                        log("Found key, try to read text and move to next iteration, current result = $result, depth = ${parser.depth}")
                        break
                    }
                }
            }
            log("Final result = $result")
            result
        } catch (e: Exception) {
            null
        }
    }

    private fun createParser(): XmlPullParser = Xml.newPullParser().apply {
        setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        setInput(StringReader(data))
    }

    private fun isKeyFound(
        parser: XmlPullParser,
        keyPart: String,
        keyPartIndex: Int
    ): Boolean {
        return parser.eventType == XmlPullParser.START_TAG && keyPartIndex.inc() == parser.depth && parser.name == keyPart
    }

    //region TODO: Delete
    private fun logParser(parser: XmlPullParser) {
        log("Type = ${getType(parser)}, name = ${parser.name}, depth = ${parser.depth}, text = ${parser.text}")
    }

    private fun log(message: String) {
        Log.d("XmlResponseData", message)
    }

    private fun getType(parser: XmlPullParser) = when (parser.eventType) {
        XmlPullParser.START_DOCUMENT -> "START_DOCUMENT"
        XmlPullParser.START_TAG -> "START_TAG"
        XmlPullParser.END_TAG -> "END_TAG"
        XmlPullParser.TEXT -> "TEXT"
        XmlPullParser.END_DOCUMENT -> "END_DOCUMENT"
        else -> "UNKNOWN"
    }

    companion object {

        const val TEST_DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<root>\n" +
                "  <args/>\n" +
                "  <data>{&quot;payment_card_number&quot;:&quot;4111111111111111&quot;,&quot;payment_card_expiration_date&quot;:&quot;12\\/2024&quot;}</data>\n" +
                "  <files/>\n" +
                "  <form/>\n" +
                "  <headers>\n" +
                "    <Accept-Encoding>gzip</Accept-Encoding>\n" +
                "    <B3>62cd35f4c6abc1230f0e40bc9f9ba8fc-5b1f5a1464822355-1</B3>\n" +
                "    <Connection>close</Connection>\n" +
                "    <Content-Length>84</Content-Length>\n" +
                "    <Content-Type>application/json</Content-Type>\n" +
                "    <Host>echo.apps.verygood.systems</Host>\n" +
                "    <User-Agent>okhttp/4.9.1</User-Agent>\n" +
                "    <Vgs-Request-Id>62cd35f4c6abc1230f0e40bc9f9ba8fc</Vgs-Request-Id>\n" +
                "    <Vgs-Tenant>tntpszqgikn</Vgs-Tenant>\n" +
                "    <X-Amzn-Trace-Id>Root=1-61f2774f-18237b723d292b5f63ed10a3</X-Amzn-Trace-Id>\n" +
                "    <X-B3-Sampled>1</X-B3-Sampled>\n" +
                "    <X-B3-Spanid>146e751f5c75e041</X-B3-Spanid>\n" +
                "    <X-B3-Traceid>62cd35f4c6abc1230f0e40bc9f9ba8fc</X-B3-Traceid>\n" +
                "    <X-Forwarded-Host>tntpszqgikn.sandbox.verygoodproxy.com</X-Forwarded-Host>\n" +
                "  </headers>\n" +
                "  <json>\n" +
                "    <payment_card_expiration_date>12/2024</payment_card_expiration_date>\n" +
                "    <payment_card_number>4111111111111111</payment_card_number>\n" +
                "  </json>\n" +
                "  <origin>93.75.214.23, 10.6.30.134, 34.194.18.145, 10.35.108.217</origin>\n" +
                "  <url>https://echo.apps.verygood.systems/post</url>\n" +
                "</root>"
    }
    //endregion
}