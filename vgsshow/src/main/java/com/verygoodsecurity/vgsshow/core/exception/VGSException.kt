package com.verygoodsecurity.vgsshow.core.exception

import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpBodyFormat

internal sealed class VGSException : Exception() {

    abstract val code: Int

    protected abstract val errorMessage: String?

    override val message: String?
        get() = errorMessage

    class Exception constructor(
        override val code: Int = -1,
        override val errorMessage: String?
    ) : VGSException()

    class UrlNotValid : VGSException() {

        override val code: Int
            get() = 1480

        override val errorMessage: String = "URL Error: Not valid organization parameters"
    }

    class NoInternetPermission : VGSException() {

        override val code: Int
            get() = 1481

        override val errorMessage: String = "Permission denied (missing INTERNET permission?)"
    }

    class NoInternetConnection : VGSException() {

        override val code: Int
            get() = 1482

        override val errorMessage: String = "No Internet connection"
    }

    class RequestTimeout : VGSException() {

        override val code: Int
            get() = 1483

        override val errorMessage: String = "TimeoutException"
    }

    class ResponsePayloadException : VGSException() {

        override val code: Int
            get() = 1401

        override val errorMessage: String = "Unexpected Response Data Format"
    }

    class RequestPayloadException constructor(httpPayloadFormat: VGSHttpBodyFormat): VGSException() {

        override val code: Int
            get() = 1404

        override val errorMessage: String = "${httpPayloadFormat.name} payload is invalid!"
    }
}