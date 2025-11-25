package com.verygoodsecurity.vgsshow.core.exception

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat

/**
 * The base class for all exceptions thrown by the VGS Show SDK.
 *
 * @property code The error code associated with the exception.
 * @property message A human-readable description of the error.
 */
sealed class VGSException : Exception() {

    abstract val code: Int

    protected abstract val errorMessage: String?

    override val message: String?
        get() = errorMessage

    /**
     * A custom exception with a user-defined error message.
     * @suppress Not for public use.
     *
     * @param code The error code.
     * @param errorMessage The error message.
     */
    internal class Custom(
        override val code: Int = -1,
        override val errorMessage: String?
    ) : VGSException()

    /**
     * Thrown when the provided URL is not valid.
     * @suppress Not for public use.
     */
    internal class UrlNotValid : VGSException() {

        override val code: Int
            get() = 1480

        override val errorMessage: String = "URL Error: Not valid organization parameters"
    }

    /**
     * Thrown when the application does not have the INTERNET permission.
     * @suppress Not for public use.
     */
    internal class NoInternetPermission : VGSException() {

        override val code: Int
            get() = 1481

        override val errorMessage: String = "Permission denied (missing INTERNET permission?)"
    }

    /**
     * Thrown when there is no active internet connection.
     * @suppress Not for public use.
     */
    internal class NoInternetConnection : VGSException() {

        override val code: Int
            get() = 1482

        override val errorMessage: String = "No Internet connection"
    }

    /**
     * Thrown when a network request times out.
     * @suppress Not for public use.
     */
    internal class RequestTimeout : VGSException() {

        override val code: Int
            get() = 1483

        override val errorMessage: String = "TimeoutException"
    }

    /**
     * Thrown when the response payload has an unexpected format.
     * @suppress Not for public use.
     */
    internal class ResponsePayload : VGSException() {

        override val code: Int
            get() = 1401

        override val errorMessage: String = "Unexpected Response Data Format"
    }

    /**
     * Thrown when the request payload is not valid for the specified format.
     * @suppress Not for public use.
     */
    internal class RequestPayload(httpPayloadFormat: VGSHttpBodyFormat) :
        VGSException() {

        override val code: Int
            get() = 1404

        override val errorMessage: String = "Payload is not valid  ${httpPayloadFormat.name}"
    }

    /**
     * Thrown when the revealed image data is not a valid image.
     */
    class ImageNotValid : VGSException() {

        override val code: Int
            get() = 1407

        override val errorMessage: String = "Base64 data is not valid image"
    }
}