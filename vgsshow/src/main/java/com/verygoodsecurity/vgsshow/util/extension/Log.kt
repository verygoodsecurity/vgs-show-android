package com.verygoodsecurity.vgsshow.util.extension

import android.util.Log
import com.verygoodsecurity.vgsshow.BuildConfig

internal fun Any.logDebug(message: String, tag: String? = this::class.simpleName) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}