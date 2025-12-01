package com.verygoodsecurity.vgsshow.core.network.headers

/**
 * An interface for a store of static headers that will be added to all requests.
 * @suppress Not for public use.
 */
internal interface StaticHeadersStore {

    /**
     * Adds a custom header.
     *
     * @param key The name of the header.
     * @param value The value of the header.
     */
    fun add(key: String, value: String)

    /**
     * Removes a custom header.
     *
     * @param key The name of the header to remove.
     */
    fun remove(key: String)

    /**
     * Returns all headers, including default and custom ones.
     *
     * @return A map of all headers.
     */
    fun getAll(): Map<String, String>

    /**
     * Returns only the custom headers set by the user.
     *
     * @return A map of custom headers.
     */
    fun getCustom(): Map<String, String>

    /**
     * Clears all custom headers.
     */
    fun clear()
}