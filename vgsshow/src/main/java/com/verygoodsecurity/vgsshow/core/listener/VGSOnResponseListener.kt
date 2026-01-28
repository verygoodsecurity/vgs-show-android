package com.verygoodsecurity.vgsshow.core.listener

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

/**
 * A listener for tracking VGS Show responses.
 */
interface VGSOnResponseListener {

    /**
     * Called when a VGS Show request receives a response from the server.
     * This callback is executed on the main thread.
     *
     * @param response The response from the VGS proxy. This can be either a [VGSResponse.Success] or a [VGSResponse.Error].
     */
    @MainThread
    fun onResponse(response: VGSResponse)
}