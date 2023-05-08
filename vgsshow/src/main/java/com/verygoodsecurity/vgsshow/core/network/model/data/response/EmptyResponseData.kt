package com.verygoodsecurity.vgsshow.core.network.model.data.response

import kotlinx.parcelize.Parcelize

@Parcelize
internal class EmptyResponseData : ResponseData {

    override fun getValue(key: String): String? = null
}