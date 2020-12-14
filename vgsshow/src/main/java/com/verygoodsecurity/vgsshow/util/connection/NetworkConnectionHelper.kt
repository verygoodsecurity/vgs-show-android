package com.verygoodsecurity.vgsshow.util.connection

internal interface NetworkConnectionHelper {

    fun isNetworkPermissionsGranted(): Boolean

    fun isNetworkConnectionAvailable(): Boolean
}