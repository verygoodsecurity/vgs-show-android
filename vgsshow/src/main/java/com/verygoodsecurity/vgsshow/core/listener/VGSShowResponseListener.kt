package com.verygoodsecurity.vgsshow.core.listener

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

interface VGSShowResponseListener {

    @MainThread
    fun onResponse(response: VGSResponse)
}