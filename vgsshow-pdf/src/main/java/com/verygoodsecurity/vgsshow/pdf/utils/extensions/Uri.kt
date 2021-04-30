package com.verygoodsecurity.vgsshow.pdf.utils.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat

//internal fun Uri.toShareIntent(title: String, message: String, mime: String, bytes: ByteArray) = Intent().apply {
//    action = Intent.ACTION_SEND
//    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//    type = mime
//    data = this@toShareIntent // Without this line error printed (unexpected behavior)
//    putExtra(Intent.EXTRA_STREAM, bytes)
//    putExtra(Intent.EXTRA_TITLE, title)
//    putExtra(Intent.EXTRA_TEXT, message)
//}

internal fun Uri.toShareIntent(
    activity: Activity,
    title: String,
    message: String,
    mime: String
) = ShareCompat.IntentBuilder.from(activity)
    .setChooserTitle(title)
    .setText(message)
    .setType(mime)
    .setStream(this)
    .intent.apply { flags = Intent.FLAG_GRANT_READ_URI_PERMISSION }