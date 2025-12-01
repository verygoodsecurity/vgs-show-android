package com.verygoodsecurity.vgsshow.core

import java.util.*

/**
 * Represents a unique session for VGS Show interactions.
 * This is used to group related analytics events.
 * @suppress Not for public use.
 */
internal object Session {

    /**
     * A unique identifier for the current session.
     */
    val id = UUID.randomUUID().toString()
}