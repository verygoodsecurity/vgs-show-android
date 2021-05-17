package com.verygoodsecurity.vgsshow.util.extension

import java.io.File
import java.io.FileOutputStream

internal fun ByteArray?.toFile(parent: File, name: String): File? = this?.let {
    val result = File(parent, name)
    FileOutputStream(result).use { fos -> fos.write(it) }
    result
}