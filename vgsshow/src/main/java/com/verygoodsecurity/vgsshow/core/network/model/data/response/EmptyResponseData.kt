package com.verygoodsecurity.vgsshow.core.network.model.data.response

import kotlinx.parcelize.Parcelize

/**
 * Represents an empty response data.
 * @suppress Not for public use.
 */
@Parcelize
internal class EmptyResponseData : ResponseData {

    override fun getValue(key: String): String? = null
}