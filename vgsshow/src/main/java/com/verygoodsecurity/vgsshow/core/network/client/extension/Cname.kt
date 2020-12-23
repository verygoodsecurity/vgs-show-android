package com.verygoodsecurity.vgsshow.core.network.client.extension

import com.verygoodsecurity.vgsshow.util.extension.toHost

internal fun String.toHostnameValidationUrl(vaultId: String): String {
    return String.format(
        "https://js.verygoodvault.com/collect-configs/%s__%s.txt",
        this.toHost(),
        vaultId
    )
}
