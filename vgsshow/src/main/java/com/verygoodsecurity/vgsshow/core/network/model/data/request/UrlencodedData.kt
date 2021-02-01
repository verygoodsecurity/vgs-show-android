package com.verygoodsecurity.vgsshow.core.network.model.data.request

import android.util.Base64

internal class UrlencodedData(data: String) : RequestData {

    private val data: String? = try {
        Base64.encodeToString(data.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }

    override fun getRawData(): String? = data

    override fun getData(): ByteArray? = data?.toByteArray(Charsets.UTF_8)

    override fun isValid(): Boolean = !data.isNullOrEmpty()
}