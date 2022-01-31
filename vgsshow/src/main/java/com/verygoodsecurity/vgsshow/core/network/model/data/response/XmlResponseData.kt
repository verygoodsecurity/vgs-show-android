package com.verygoodsecurity.vgsshow.core.network.model.data.response

import android.util.Xml
import androidx.annotation.VisibleForTesting
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

internal class XmlResponseData constructor(private val data: String) : ResponseData {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var parser: XmlPullParser? = null

    override fun getValue(key: String): String? = read(key)

    private fun read(key: String): String? {
        return try {
            var result: String? = null
            val parser = createParser()
            val keyParts = key.split(".")
            keyParts.forEachIndexed { index, keyPart ->
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (isKeyFound(parser, keyPart, index)) {
                        parser.next()
                        result = if (index == keyParts.lastIndex) parser.text else null
                        break
                    }
                }
            }
            result
        } catch (e: Exception) {
            null
        }
    }

    private fun createParser(): XmlPullParser = parser ?: Xml.newPullParser().apply {
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
}