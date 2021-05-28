package com.verygoodsecurity.vgsshow.core.network.client

import com.verygoodsecurity.vgsshow.core.network.client.httpurl.TLSSocketFactory
import org.junit.Assert.assertTrue
import org.junit.Test

class TLSSocketFactoryTest {

    @Test
    fun test_socket_factory() {
        val protocol = "custom_protocols"
        val tlsVersionOne = "TLSv1.1"
        val tlsVersionTwo = "TLSv1.2"

        val connectionProtocols = arrayOf(protocol)

        val protocols = TLSSocketFactory().mergeProtocols(connectionProtocols)

        assertTrue(arrayOf(protocol, tlsVersionOne, tlsVersionTwo).contentEquals(protocols))
    }
}