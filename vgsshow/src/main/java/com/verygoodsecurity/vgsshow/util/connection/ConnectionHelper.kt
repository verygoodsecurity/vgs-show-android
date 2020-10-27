package com.verygoodsecurity.vgsshow.util.connection

import android.content.Context
import android.net.ConnectivityManager
import java.lang.ref.WeakReference

class ConnectionHelper(context: Context) : IConnectionHelper {

    private val context: WeakReference<Context> = WeakReference(context)

    override fun isConnectionAvailable(): Boolean {
        val manager = context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return (manager as? ConnectivityManager)?.activeNetworkInfo != null
    }
}