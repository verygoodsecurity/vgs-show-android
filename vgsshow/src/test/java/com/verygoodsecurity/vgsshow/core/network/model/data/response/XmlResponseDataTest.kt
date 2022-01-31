package com.verygoodsecurity.vgsshow.core.network.model.data.response

import android.util.Xml
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

@RunWith(RobolectricTestRunner::class)
class XmlResponseDataTest {

    private lateinit var xmlPullParser: XmlPullParser

    @Before
    fun setup() {
        xmlPullParser = Xml.newPullParser()
        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    }

    @Test
    fun getValue_oneLevelOfDepth_successfullyReturned() {
        // Arrange
        val xml = "<tag>Test</tag>"
        xmlPullParser.setInput(StringReader(xml))
        val responseData = XmlResponseData(xml)
        responseData.parser = xmlPullParser
        val expectedResult = "Test"

        // Act
        val result = responseData.getValue("tag")
        //Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun getValue_twoLevelOfDepth_successfullyReturned() {
        // Arrange
        val xml = "<tag>\n<nested_tag>Test</nested_tag>\n</tag>"
        xmlPullParser.setInput(StringReader(xml))
        val responseData = XmlResponseData(xml)
        responseData.parser = xmlPullParser
        val expectedResult = "Test"

        // Act
        val result = responseData.getValue("tag.nested_tag")
        //Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun getValue_treeLevelOfDepthComplexXml_successfullyReturned() {
        // Arrange
        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<tag>\n" +
                "  <tag1/>\n" +
                "  <tag2/>\n" +
                "  <tag3>\n" +
                "    <tag4>Test</tag4>\n" +
                "  </tag3>\n" +
                "  <tag5>\n" +
                "    <tag6>Test</tag6>\n" +
                "  </tag5>\n" +
                "</tag>"
        xmlPullParser.setInput(StringReader(xml))
        val responseData = XmlResponseData(xml)
        responseData.parser = xmlPullParser
        val expectedResult = "Test"

        // Act
        val result = responseData.getValue("tag.tag5.tag6")
        //Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun getValue_twoLevelOfDepth_doesNotIgnoreCase() {
        // Arrange
        val xml = "<Tag>\n<nested_tag>Test</nested_tag>\n</Tag>"
        xmlPullParser.setInput(StringReader(xml))
        val responseData = XmlResponseData(xml)
        responseData.parser = xmlPullParser
        val expectedResult = null

        // Act
        val result = responseData.getValue("tag.nested_tag")
        //Assert
        Assert.assertEquals(expectedResult, result)
    }
}