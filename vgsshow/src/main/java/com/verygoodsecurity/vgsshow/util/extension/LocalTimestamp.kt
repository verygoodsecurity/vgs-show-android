package com.verygoodsecurity.vgsshow.util.extension

/**
 * A utility object for getting the current timestamp.
 * @suppress Not for public use.
 */
internal object LocalTimestamp {

    /**
     * Returns the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    fun get() = System.currentTimeMillis()
}