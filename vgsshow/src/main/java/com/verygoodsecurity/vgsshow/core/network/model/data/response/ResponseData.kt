package com.verygoodsecurity.vgsshow.core.network.model.data.response

import android.os.Parcelable

internal interface ResponseData: Parcelable {

    fun getValue(key: String): String?
}