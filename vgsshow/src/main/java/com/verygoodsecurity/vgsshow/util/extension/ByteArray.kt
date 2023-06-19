package com.verygoodsecurity.vgsshow.util.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory

internal fun ByteArray.decodeBitmap(): Bitmap? {
    return BitmapFactory.decodeByteArray(this, 0, size)
}