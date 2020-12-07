package com.verygoodsecurity.vgsshow.core.network.client.extension

import java.net.MalformedURLException
import java.net.URL

internal fun String.toHostnameValidationUrl(vaultId: String): String {
    return String.format(
        "https://js.verygoodvault.com/collect-configs/%s__%s.txt",
        this.toHost(),
        vaultId
    )
}

internal fun String.toHost(): String {
    return try {
        URL(this.toHttps()).host
    } catch (e: MalformedURLException) {
        ""
    }
}

internal fun String.toHttps(): String {
    return when {
        startsWith("http://") -> this
        startsWith("https://") -> this
        else -> "https://$this"
    }
}
