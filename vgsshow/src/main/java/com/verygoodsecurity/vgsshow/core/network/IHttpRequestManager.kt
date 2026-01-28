package com.verygoodsecurity.vgsshow.core.network

import android.os.NetworkOnMainThreadException
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

/**
 * An interface for managing HTTP requests.
 * @suppress Not for public use.
 */
internal interface IHttpRequestManager {

    /**
     * Executes a [VGSRequest] synchronously.
     * This method should be called on a worker thread.
     *
     * @param request The request to execute.
     * @return The response from the server.
     * @throws NetworkOnMainThreadException if called on the main thread.
     */
    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun execute(request: VGSRequest): VGSResponse

    /**
     * Enqueues a [VGSRequest] to be executed asynchronously.
     *
     * @param request The request to enqueue.
     * @param callback The callback to be invoked when the request is complete.
     */
    fun enqueue(request: VGSRequest, callback: ((VGSResponse) -> Unit)?)

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