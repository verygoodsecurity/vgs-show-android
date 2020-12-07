package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.httpurl.TLSSocketFactory
import org.junit.Assert.*
import org.junit.Test

class TLSSocketFactoryTest {

    @Test
    fun test_socket_factory() {
        val protocol = "custom_protocols"
        val tls_1_1 = "TLSv1.1"
        val tls_1_2 = "TLSv1.2"

        val connectionProtocols = arrayOf(protocol)

        val protocols = TLSSocketFactory().mergeProtocols(connectionProtocols)

        assertTrue(arrayOf(protocol, tls_1_1, tls_1_2).contentEquals(protocols))
    }
}