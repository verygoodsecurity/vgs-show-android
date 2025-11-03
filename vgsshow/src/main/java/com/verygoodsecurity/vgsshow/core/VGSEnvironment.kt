package com.verygoodsecurity.vgsshow.core

import com.verygoodsecurity.vgsshow.util.extension.concatWithDash

/**
 *
 * Defines the VGS environment to which `VGSShow` will connect. For more information, see the
 * [documentation](https://www.verygoodsecurity.com/docs/getting-started/going-live#sandbox-vs-live).
 *
 * @property value The unique identifier for the environment.
 */
sealed class VGSEnvironment {

    abstract val value: String

    /**
     *  Connects to the live environment.
     *  
     *  @param suffix The suffix for the environment, e.g., "eu-2" for "live-eu-2".
     */
    data class Live(val suffix: String = "") : VGSEnvironment() {

        override val value: String
            get() = DEFAULT_VALUE concatWithDash suffix

        override fun toString(): String {
            return value
        }

        companion object {
            internal const val DEFAULT_VALUE = "live"
        }
    }

    /**
     *  Connects to the sandbox environment.
     *
     *  @param suffix The suffix for the environment, e.g., "eu-2" for "sandbox-eu-2".
     */
    data class Sandbox(val suffix: String = "") : VGSEnvironment() {

        override val value: String
            get() = DEFAULT_VALUE concatWithDash suffix

        override fun toString(): String {
            return value
        }

        companion object {
            internal const val DEFAULT_VALUE = "sandbox"
        }
    }

    internal object Empty : VGSEnvironment() {

        private const val DEFAULT_VALUE = ""

        override val value: String
            get() = DEFAULT_VALUE
    }

    companion object {

        private const val ENV_REGEX = "^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$"

        /**
         * Returns `true` if this `VGSEnvironment` is valid, `false` otherwise.
         */
        fun VGSEnvironment.isValid() = ENV_REGEX.toPattern().matcher(this.value).matches()

        /**
         * Returns `true` if this `String` is a valid environment name, `false` otherwise.
         */
        fun String.isValidEnvironment() = ENV_REGEX.toPattern().matcher(this).matches()

        /**
         * Converts this `String` to a `VGSEnvironment`, e.g., "live-eu-3" becomes `VGSEnvironment.Live("-eu-3")`.
         */
        fun String.toVGSEnvironment(): VGSEnvironment = if (isValidEnvironment()) when {
            contains(Live.DEFAULT_VALUE, true) -> {
                Live(this.substring(Live.DEFAULT_VALUE.length, this.length))
            }
            contains(Sandbox.DEFAULT_VALUE, true) -> {
                Sandbox(this.substring(Sandbox.DEFAULT_VALUE.length, this.length))
            }
            else -> Empty
        } else {
            Empty
        }
    }
}