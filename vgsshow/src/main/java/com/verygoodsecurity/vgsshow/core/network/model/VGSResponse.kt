package com.verygoodsecurity.vgsshow.core.network.model

sealed class VGSResponse {

    abstract val code: Int

    data class Success constructor(
        override val code: Int,
        val data: Map<String, Any>?,
        val raw: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code \n $raw"
    }

    data class Error constructor(
        override val code: Int,
        val message: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code \n $message"
    }
}