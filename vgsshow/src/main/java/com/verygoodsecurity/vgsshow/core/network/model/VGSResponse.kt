package com.verygoodsecurity.vgsshow.core.network.model

import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData

/**
 * The base class definition for a VGSShow response states.
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
        internal val data: ResponseData
    ) : VGSResponse() {

        override fun toString() = "Code: $code"

        internal companion object {

            fun create(code: Int, data: ResponseData) = Success(code, data)
        }
    }

    /**
     * The class definition for an error response state.
     */
    class Error private constructor(
        override val code: Int,
        val message: String?
    ) : VGSResponse() {

        override fun toString() = "Code: $code \n $message"

        internal companion object {

            fun create(exception: VGSException) = Error(exception.code, exception.message)
        }
    }
}