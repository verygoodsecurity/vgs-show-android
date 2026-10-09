package com.verygoodsecurity.vgsshow.util.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class MapperKtTest {

    @Test
    fun analyticsUI_xmlView_xml() {
        assertEquals("xml", analyticsUI(isCompose = false))
    }

    @Test
    fun analyticsUI_composeWrapper_compose() {
        assertEquals("compose", analyticsUI(isCompose = true))
    }

    @Test
    fun nativeComposeAnalyticsUI_nativeCompose() {
        assertEquals("native-compose", nativeComposeAnalyticsUI())
    }
}
