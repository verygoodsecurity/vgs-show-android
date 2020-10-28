package com.verygoodsecurity.vgsshow.core

/**
 *
 * Define type of Vault for VGSShow to communicate with.
 *
 * @param rawValue Unique identifier.
 *
 * @version 1.0
 */
enum class Environment(val rawValue: String) {

    /**
     *  Sandbox Environment using sandbox Test Vault
     */
    SANDBOX("sandbox"),

    /**
     *  Live Environment using Live Vault
     */
    LIVE("live")
}

internal fun String.isSandbox(): Boolean = this.contains(Environment.SANDBOX.rawValue)

internal fun String.isLive(): Boolean = this.contains(Environment.LIVE.rawValue)
