package com.verygoodsecurity.vgsshow.core.network.client

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpRequest
import com.verygoodsecurity.vgsshow.core.network.client.model.HttpResponse

internal const val CONTENT_TYPE = "Content-type"

/**
 * An interface for making HTTP requests.
 * @suppress Not for public use.
 */
internal interface IHttpClient {

    /**
     * Executes an [HttpRequest] synchronously.
     * This method should be called on a worker thread.
     *
     * @param request The request to execute.
     * @return The response from the server.
     * @throws Exception if an error occurs during the request.
     */
    @WorkerThread
    @Throws(Exception::class)
    fun execute(request: HttpRequest): HttpResponse

    /**
     * Enqueues an [HttpRequest] to be executed asynchronously.
     *
     * @param request The request to enqueue.
     * @param callback The callback to be invoked when the request is complete.
     */
    fun enqueue(request: HttpRequest, callback: HttpRequestCallback)

    /**
     * Sets a CNAME for the given vault.
     *
     * @param vaultId The ID of the vault.
     * @param cname The CNAME to set.
     * @param cnameResult The callback to be invoked with the result of the CNAME resolution.
     */
    fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit)

    /**
     * Cancels all pending requests.
     */
    fun cancelAll()
}