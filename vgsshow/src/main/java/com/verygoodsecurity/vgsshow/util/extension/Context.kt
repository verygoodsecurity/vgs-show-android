package com.verygoodsecurity.vgsshow.util.extension

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Checks if the given permissions are granted.
 * @suppress Not for public use.
 *
 * @param permissions The permissions to check.
 * @return `true` if all permissions are granted, `false` otherwise.
 */
internal fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED) {
            return false
        }
    }
    return true
}