package com.verygoodsecurity.vgsshow.util.extension

import android.util.Log
import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.logs.VGSLogger

internal fun Any.logDebug(message: String, tag: String? = this::class.simpleName) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}

internal fun Any.logDebug(message: String, vararg values: Any?) {
    VGSLogger.debug(this::class.java.simpleName, message, *values)
}

internal fun Any.logWaring(message: String, vararg values: Any?, tag: String? = null) {
    VGSLogger.warning((tag ?: this::class.java.simpleName), message, *values)
}