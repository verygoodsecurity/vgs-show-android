package com.verygoodsecurity.vgsshow.core.exception

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat

/**
 * The base class for all exceptions thrown by the VGS Show SDK.
 */
sealed class VGSException : Exception() {

    abstract val code: Int

    protected abstract val errorMessage: String?

    override val message: String?
        get() = errorMessage

    internal class Custom(
        override val code: Int = -1,
        override val errorMessage: String?
    ) : VGSException()

    internal class UrlNotValid : VGSException() {

        override val code: Int
            get() = 1480

        override val errorMessage: String = "URL Error: Not valid organization parameters"
    }

    internal class NoInternetPermission : VGSException() {

        override val code: Int
            get() = 1481

        override val errorMessage: String = "Permission denied (missing INTERNET permission?)"
    }

    internal class NoInternetConnection : VGSException() {

        override val code: Int
            get() = 1482

        override val errorMessage: String = "No Internet connection"
    }

    internal class RequestTimeout : VGSException() {

        override val code: Int
            get() = 1483

        override val errorMessage: String = "TimeoutException"
    }

    internal class ResponsePayload : VGSException() {

        override val code: Int
            get() = 1401

        override val errorMessage: String = "Unexpected Response Data Format"
    }

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