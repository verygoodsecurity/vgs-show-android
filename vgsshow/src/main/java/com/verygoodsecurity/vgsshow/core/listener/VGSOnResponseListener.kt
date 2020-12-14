package com.verygoodsecurity.vgsshow.core.listener

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

/**
 * Interface definition for a receiving callback.
 */
interface VGSOnResponseListener {

    /**
     * Callback that called after request execution complete with success of failure response.
     *
     * @param response successful or failure response. @see [com.verygoodsecurity.vgsshow.core.network.model.VGSResponse]
     */
    @MainThread
    fun onResponse(response: VGSResponse)
}