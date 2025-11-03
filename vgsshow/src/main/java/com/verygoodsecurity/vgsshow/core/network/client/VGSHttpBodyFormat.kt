package com.verygoodsecurity.vgsshow.core.network.client

/**
 * The format of the request body.
 */
enum class VGSHttpBodyFormat {

    /**
     * A JSON object.
     */
    JSON,

    /**
     * A URL-encoded form.
     */
    X_WWW_FORM_URLENCODED,
}