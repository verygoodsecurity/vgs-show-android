package com.verygoodsecurity.vgsshow.widget.view.textview.method

class VGSSecureTextOptions private constructor(val ignoreTransformationRegex: Boolean) {

    class Builder {

        private var ignoreTransformationRegex: Boolean = false

        fun isIgnoreTransformationRegex(isIgnore: Boolean) =
            apply { this.ignoreTransformationRegex = isIgnore }

        fun build() = VGSSecureTextOptions(ignoreTransformationRegex)
    }
}