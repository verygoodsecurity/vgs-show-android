package com.verygoodsecurity.vgsshow.core.network.model.data.request

/**
 * An interface for representing request data.
 * @suppress Not for public use.
 */
internal interface RequestData {

    /**
     * Returns the raw string representation of the data.
     */
    fun getRawData(): String?

    /**
     * Returns the data as a byte array.
     */
    fun getData(): ByteArray?

    /**
     * Returns `true` if the data is valid, `false` otherwise.
     */
    fun isValid(): Boolean
}