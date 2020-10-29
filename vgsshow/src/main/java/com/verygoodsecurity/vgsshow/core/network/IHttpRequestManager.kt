package com.verygoodsecurity.vgsshow.core.network

import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

internal interface IHttpRequestManager {

    @WorkerThread
    fun execute(request: VGSRequest): VGSResponse
}