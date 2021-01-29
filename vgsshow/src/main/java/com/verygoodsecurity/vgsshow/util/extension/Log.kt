package com.verygoodsecurity.vgsshow.util.extension

import com.verygoodsecurity.vgsshow.core.logs.VGSLogger

internal fun Any.logDebug(message: String, tag: String? = null) {
    VGSLogger.debug((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logWaring(message: String, tag: String? = null) {
    VGSLogger.warning((tag ?: this::class.java.simpleName), message)
}

internal fun Any.logRequest(url: String, headers: Map<String, String>, ) {

}

internal fun Any.logResponse() {

}