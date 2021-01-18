package com.verygoodsecurity.vgsshow.widget.view.textview.extension

import android.text.method.TransformationMethod
import android.widget.TextView

fun TextView.updateTransformationMethod(method: TransformationMethod) {
    this.transformationMethod = null
    this.transformationMethod = method
}