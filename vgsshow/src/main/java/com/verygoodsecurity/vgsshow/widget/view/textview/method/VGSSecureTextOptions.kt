package com.verygoodsecurity.vgsshow.widget.view.textview.method

private const val DEFAULT_SECURE_REPLACEMENT = '•'

/**
 * Secure text options definition class.
 */
class VGSSecureTextOptions private constructor(val secureSymbol: Char) {

    class Builder {

        private var secureSymbol: Char = DEFAULT_SECURE_REPLACEMENT

        /**
         *  Apply custom secure symbol.
         *
         *  @param secureSymbol custom secure char.
         */
        fun setSecureSymbol(secureSymbol: Char) = apply { this.secureSymbol = secureSymbol }

        /**
         * Build VGSSecureTextOptions object.
         *
         * @return configured VGSSecureTextOptions.
         */
        fun build() = VGSSecureTextOptions(secureSymbol)
    }
}