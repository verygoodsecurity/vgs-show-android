package com.verygoodsecurity.vgsshow.core.network.client

/**
 * An abstract base class for implementing [IHttpClient].
 * @suppress Not for public use.
 *
 * @param isLogsEnabled Whether or not to enable logging.
 */
abstract class BaseHttpClient constructor(protected var isLogsEnabled: Boolean) : IHttpClient