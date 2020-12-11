package com.verygoodsecurity.vgsshow.util.connection

internal interface IConnectionHelper {

    fun isNetworkPermissionsGranted(): Boolean

    fun isConnectionAvailable(): Boolean
}