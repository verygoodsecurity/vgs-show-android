package com.verygoodsecurity.vgsshow.core.network

import android.os.NetworkOnMainThreadException
import androidx.annotation.WorkerThread
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

internal interface IHttpRequestManager {

    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    fun execute(request: VGSRequest): VGSResponse

    fun enqueue(request: VGSRequest, callback: ((VGSResponse) -> Unit)?)

    fun setCname(vaultId: String, cname: String?, cnameResult: (Boolean, Long) -> Unit)

    fun cancelAll()
}