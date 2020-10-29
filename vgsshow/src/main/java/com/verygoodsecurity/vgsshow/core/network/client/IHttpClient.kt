package com.verygoodsecurity.vgsshow.core.network.client

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

// TODO: refactor, send content type as parameter to make this class reusable
internal const val CONTENT_TYPE = "Content-type"
internal const val APPLICATION_JSON = "application/json"

internal const val CONNECTION_TIME_OUT = 60000L

internal interface IHttpClient {

    @WorkerThread
    @Throws(Exception::class)
    fun call(request: HttpRequest): HttpResponse

    @AnyThread
    fun enqueue(request: HttpRequest)
}