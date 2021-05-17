package com.verygoodsecurity.vgsshow.util.extension

import android.content.Intent
import android.net.Uri

internal fun Uri.toShareIntent(title: String, message: String, mime: String) = Intent().apply {
    action = Intent.ACTION_SEND
    type = mime
    putExtra(Intent.EXTRA_STREAM, this@toShareIntent)
    putExtra(Intent.EXTRA_TITLE, title)
    putExtra(Intent.EXTRA_TEXT, message)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}

