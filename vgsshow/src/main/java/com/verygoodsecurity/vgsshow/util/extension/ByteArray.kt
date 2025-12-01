package com.verygoodsecurity.vgsshow.util.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.verygoodsecurity.vgsshow.core.exception.VGSException

/**
 * Decodes a [Bitmap] from this byte array.
 * @suppress Not for public use.
 *
 * @return The decoded bitmap.
 * @throws VGSException.ImageNotValid if the byte array cannot be decoded into a bitmap.
 */
@Throws(VGSException.ImageNotValid::class)
internal fun ByteArray.decodeBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size) ?: throw VGSException.ImageNotValid()
}