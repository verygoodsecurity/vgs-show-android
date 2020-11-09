package com.verygoodsecurity.vgsshow.core.network.headers

/**
 * Interface for static headers store that will be added to all requests.
 *
 * @since 1.0.0
 */
interface IVGSStaticHeadersStore {

    /**
     * Add custom header. Does not allow duplication.
     *
     * @param key http request header name, ex. "Authorization".
     * @param value http request header name, ex. "authorization_token".
     */
    fun add(key: String, value: String)

    /**
     * Remove custom header.
     *
     * @param key http request header name, ex. "Authorization".
     */
    fun remove(key: String)

    /**
     * @return all custom headers.
     */
    fun getAll(): Map<String, String>

    /**
     * Clear all headers.
     */
    fun clear()
}