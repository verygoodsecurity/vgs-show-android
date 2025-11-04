package com.verygoodsecurity.vgsshow.core.network.model

import android.os.Parcelable
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import kotlinx.parcelize.Parcelize

/**
 * The base class for VGS Show SDK responses.
 *
 * @property code The HTTP response code.
 */
sealed class VGSResponse {

    abstract val code: Int

    /**
     * A successful response from the VGS proxy.
     *
     * @property code The HTTP response code.
     */
    @Parcelize
    class Success private constructor(
        override val code: Int,
        internal val data: ResponseData
    ) : VGSResponse(), Parcelable {

        /**
         * Returns a string representation of this `Success` response.
         */
        override fun toString() = "Code: $code"

        internal companion object {

            fun create(code: Int, data: ResponseData) = Success(code, data)
        }
    }

    /**
     * An error response from the VGS proxy.
     *
     * @property code The HTTP response code.
     * @property message The error message.
     */
    class Error private constructor(
        override val code: Int,
        val message: String?
    ) : VGSResponse() {

        /**
         * Returns a string representation of this `Error` response.
         */
        override fun toString() = "Code: $code \n $message"

        internal companion object {

            fun create(exception: VGSException) = Error(exception.code, exception.message)
        }
    }
}