package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData

sealed class VGSResponse {

    abstract val code: Int

    data class Success<T : IResponseData> constructor(
        override val code: Int,
        internal val data: T,
        internal val raw: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code"
    }

    data class Error constructor(val exception: VGSException) : VGSResponse() {

        override val code: Int = exception.code

        override fun toString() = "Code: $code \n ${exception.message}"
    }
}