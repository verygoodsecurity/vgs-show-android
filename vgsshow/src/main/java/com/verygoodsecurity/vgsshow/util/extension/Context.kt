package com.verygoodsecurity.vgsshow.util.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED) {
            return false
        }
    }
    return true
}