package com.verygoodsecurity.vgsshow.widget.view.pdf.utils.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat

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