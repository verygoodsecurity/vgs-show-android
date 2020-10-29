package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.exception.VGSException

sealed class VGSResponse {

    abstract val code: Int

    data class Success constructor(
        override val code: Int,
        val data: Map<String, Any>?,
        val raw: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code \n $raw"
    }

    data class Error constructor(val exception: VGSException) : VGSResponse() {

        override val code: Int = exception.code

        override fun toString() = "Code: $code \n ${exception.message}"
    }
}