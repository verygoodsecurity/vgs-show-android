package com.verygoodsecurity.vgsshow.util.connection

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import com.verygoodsecurity.vgsshow.util.extension.isPermissionsGranted
import java.lang.ref.WeakReference

internal class BaseNetworkConnectionHelper(context: Context) : NetworkConnectionHelper {

    private val context: WeakReference<Context> = WeakReference(context)

    override fun isNetworkPermissionsGranted() = context.get()?.isPermissionsGranted(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    ) ?: false

    /**
     * Implementation of this function is deprecated, but current new way of retrieving
     * connectivity availability is not flexible enough to be used in our case
     */
    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isNetworkConnectionAvailable(): Boolean {
        val manager = context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return (manager as? ConnectivityManager)?.activeNetworkInfo != null
    }
}