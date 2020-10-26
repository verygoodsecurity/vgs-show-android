package com.verygoodsecurity.vgsshow.core.network

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

interface INetworkManager {

    @WorkerThread
    fun execute(request: VGSRequest): VGSResponse
}