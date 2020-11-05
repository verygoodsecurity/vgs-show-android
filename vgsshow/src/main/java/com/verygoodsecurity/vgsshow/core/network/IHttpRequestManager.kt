package com.verygoodsecurity.vgsshow.core.network

import android.os.NetworkOnMainThreadException
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import kotlin.jvm.Throws

internal interface IHttpRequestManager {

    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun execute(request: VGSRequest): VGSResponse

    fun enqueue(request: VGSRequest, callback: (VGSResponse) -> Unit)

    fun cancelAll()
}