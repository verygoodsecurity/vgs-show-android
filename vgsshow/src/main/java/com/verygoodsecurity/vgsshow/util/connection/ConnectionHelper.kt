package com.verygoodsecurity.vgsshow.util.connection

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import com.verygoodsecurity.vgsshow.util.extension.isPermissionsGranted
import java.lang.ref.WeakReference

internal class ConnectionHelper(context: Context) : IConnectionHelper {

    private val context: WeakReference<Context> = WeakReference(context)

    override fun isNetworkPermissionsGranted() = context.get()?.isPermissionsGranted(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    ) ?: false

    /**
     * Implementation of this function is deprecated, but current new way of retrieving
     * connectivity availability is not flexible enough to be used in our case
     */
    override fun isConnectionAvailable(): Boolean {
        val manager = context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return (manager as? ConnectivityManager)?.activeNetworkInfo != null
    }
}