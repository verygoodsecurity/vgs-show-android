package com.verygoodsecurity.vgsshow.core.network.client

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.BuildConfig
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

internal const val APPLICATION_JSON = "application/json"
internal const val CONNECTION_TIME_OUT = 60000L

internal const val AGENT = "VGS-CLIENT"
internal const val TEMPORARY_STR_AGENT = "source=androidSDK&medium=vgs-show&content=${BuildConfig.VERSION_NAME}"

internal interface IHttpClient {

    @WorkerThread
    @Throws(Exception::class)
    fun call(request: HttpRequest): HttpResponse
}