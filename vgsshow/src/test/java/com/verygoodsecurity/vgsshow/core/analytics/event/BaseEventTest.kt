package com.verygoodsecurity.vgsshow.core.analytics.event

import com.verygoodsecurity.vgsshow.util.extension.LocalTimestamp
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Before

const val EVENT_LOCAL_TIMESTAMP = 1001L

abstract class BaseEventTest {

    @Before
    fun setup() {
        mockkObject(LocalTimestamp)
        every { LocalTimestamp.get() } returns EVENT_LOCAL_TIMESTAMP
    }
}