package com.verygoodsecurity.vgsshow.util.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.verygoodsecurity.vgsshow.core.exception.VGSException

@Throws(VGSException.ImageNotValid::class)
internal fun ByteArray.decodeBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size) ?: throw VGSException.ImageNotValid()
}