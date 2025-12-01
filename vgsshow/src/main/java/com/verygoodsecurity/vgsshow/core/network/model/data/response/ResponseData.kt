package com.verygoodsecurity.vgsshow.core.network.model.data.response

import android.os.Parcelable

/**
 * An interface for representing response data.
 * @suppress Not for public use.
 */
internal interface ResponseData : Parcelable {

    /**
     * Returns the value for the given key.
     *
     * @param key The key.
     * @return The value, or `null` if the key is not found.
     */
    fun getValue(key: String): String?
}