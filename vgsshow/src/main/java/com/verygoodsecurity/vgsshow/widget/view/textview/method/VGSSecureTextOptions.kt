package com.verygoodsecurity.vgsshow.widget.view.textview.method

private const val DEFAULT_SECURE_REPLACEMENT = '•'
private const val DEFAULT_IGNORE_TRANSFORMATION_REGEX = true

class VGSSecureTextOptions private constructor(
    val secureSymbol: Char,
    val ignoreTransformationRegex: Boolean
) {

    class Builder {

        private var secureSymbol: Char = DEFAULT_SECURE_REPLACEMENT

        private var ignoreTransformationRegex: Boolean = DEFAULT_IGNORE_TRANSFORMATION_REGEX

        fun setSecureSymbol(secureSymbol: Char) = apply { this.secureSymbol = secureSymbol }

        fun setIsIgnoreTransformationRegex(isIgnore: Boolean) =
            apply { this.ignoreTransformationRegex = isIgnore }

        fun build() = VGSSecureTextOptions(secureSymbol, ignoreTransformationRegex)
    }
}