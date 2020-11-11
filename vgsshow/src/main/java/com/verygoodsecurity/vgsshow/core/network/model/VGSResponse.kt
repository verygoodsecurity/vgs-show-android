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
    class Success private constructor(
        override val code: Int,
        internal val data: IResponseData,
        internal val raw: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code"

        internal companion object {

            fun create(code: Int, data: IResponseData, raw: String?) = Success(code, data, raw)
        }
    }

    /**
     * The class definition for an error response state.

     * @param exception exception which cause unsuccessful response. @see [com.verygoodsecurity.vgsshow.core.exception.VGSException]
     */
    class Error private constructor(internal val exception: VGSException) : VGSResponse() {

        override val code: Int = exception.code

        val message: String? = exception.message

        override fun toString() = "Code: $code \n $message"

        internal companion object {

            fun create(exception: VGSException) = Error(exception)
        }
    }
}