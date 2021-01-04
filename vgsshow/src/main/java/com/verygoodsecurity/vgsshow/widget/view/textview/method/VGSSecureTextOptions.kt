package com.verygoodsecurity.vgsshow.widget.view.textview.method

private const val DEFAULT_SECURE_REPLACEMENT = '•'
private const val DEFAULT_IGNORE_TRANSFORMATION_REGEX = true

/**
 * Secure text options definition class.
 */
class VGSSecureTextOptions private constructor(
    val secureSymbol: Char,
    val ignoreTransformationRegex: Boolean
) {

    class Builder {

        private var secureSymbol: Char = DEFAULT_SECURE_REPLACEMENT

        private var ignoreTransformationRegex: Boolean = DEFAULT_IGNORE_TRANSFORMATION_REGEX

        /**
         *  Apply custom secure symbol.
         *
         *  @param secureSymbol custom secure char.
         */
        fun setSecureSymbol(secureSymbol: Char) = apply { this.secureSymbol = secureSymbol }

        /**
         * Determine should be ignored transformation regex or not.
         *
         * @param isIgnore true if should be ignore and all symbols should be secured, false otherwise.
         */
        fun setIsIgnoreTransformationRegex(isIgnore: Boolean) =
            apply { this.ignoreTransformationRegex = isIgnore }

        /**
         * Build VGSSecureTextOptions object.
         *
         * @return configured VGSSecureTextOptions.
         */
        fun build() = VGSSecureTextOptions(secureSymbol, ignoreTransformationRegex)
    }
}