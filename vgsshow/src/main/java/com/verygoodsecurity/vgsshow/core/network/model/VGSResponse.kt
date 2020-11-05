package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData

/**
 * The base class definition for a VGSCollect response states.
 *
 * @property code The response code from server.
 *
 * @version 1.0.0
 */
sealed class VGSResponse {

    abstract val code: Int

    /**
     * The class definition for a success response state.
     */
    data class Success constructor(
        override val code: Int,
        internal val data: IResponseData,
        internal val raw: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code"
    }

    /**
     * The class definition for an error response state.

     * @param exception exception which cause unsuccessful response. @see [com.verygoodsecurity.vgsshow.core.exception.VGSException]
     */
    data class Error constructor(val exception: VGSException) : VGSResponse() {

        override val code: Int = exception.code

        override fun toString() = "Code: $code \n ${exception.message}"
    }
}