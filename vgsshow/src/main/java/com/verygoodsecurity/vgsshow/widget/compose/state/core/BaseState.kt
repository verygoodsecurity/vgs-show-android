package com.verygoodsecurity.vgsshow.widget.compose.state.core

import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType

abstract class BaseState internal constructor(
    val contentPath: String,
    val ignore: Boolean,
    val fieldType: VGSFieldType,
) {

    override fun toString(): String = "${this::class.simpleName}(contentPath=$contentPath)"
}
