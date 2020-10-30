package com.verygoodsecurity.vgsshow.core

/**
 *
 * Define type of Vault for VGSShow to communicate with.
 *
 * @property value Unique identifier.
 *
 * @version 1.0
 */
sealed class VGSEnvironment {

    abstract val value: String

    /**
     *  Live Environment using Live Vault
     *  @param suffix ex. "eu", "-eu-2", value will be "live-eu" or "live-eu-3" respectively
     */
    data class Live(val suffix: String = "") : VGSEnvironment() {

        override val value: String
            get() = DEFAULT_VALUE + if (suffix.startsWith(DASH_PREFIX)) suffix else DASH_PREFIX + suffix

        companion object {

            const val DEFAULT_VALUE = "live"
        }
    }

    /**
     *  Sandbox Environment using sandbox Test Vault
     *  @param suffix ex. "eu", "-eu-2", value will be "sandbox-eu" or "sandbox-eu-3" respectively
     */
    data class Sandbox(val suffix: String = "") : VGSEnvironment() {

        override val value: String
            get() = DEFAULT_VALUE + if (suffix.startsWith(DASH_PREFIX)) suffix else DASH_PREFIX + suffix

        companion object {

            const val DEFAULT_VALUE = "sandbox"
        }
    }

    internal object Empty : VGSEnvironment() {

        private const val DEFAULT_VALUE = ""

        override val value: String
            get() = DEFAULT_VALUE
    }

    companion object {

        private const val DASH_PREFIX = "-"

        private const val ENV_REGEX = "^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$"

        fun VGSEnvironment.isValid() = ENV_REGEX.toPattern().matcher(this.value).matches()

        fun String.isValidEnvironment() = ENV_REGEX.toPattern().matcher(this).matches()

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