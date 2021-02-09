package com.verygoodsecurity.vgsshow.core.logs

import android.util.Log

/**
 * This object is used to log messages in VGS Show SDK.
 */
object VGSShowLogger {

    private const val TAG = "VGSShow"

    /**
     * Current priority level for filtering debugging logs.
     */
    var level: Level = Level.NONE

    /**
     * Allows enable and disable debug-log printing. Disabled by default.
     */
    var isEnabled: Boolean = false

    /**
     *
     * @return true if level is DEBUG.
     */
    fun isDebugEnabled() = Level.DEBUG.ordinal >= level.ordinal

    internal fun debug(tag: String, message: String) {
        log(Level.DEBUG, tag, message)
    }

    internal fun warning(tag: String, message: String) {
        log(Level.WARN, tag, message)
    }

    private fun log(level: Level, tag: String, message: String?) {
        if (isEnabled && level.ordinal >= this.level.ordinal) {
            val msg = "$tag - $message"
            when (level) {
                Level.DEBUG -> Log.d(TAG, msg)
                Level.WARN -> Log.w(TAG, msg)
                Level.NONE -> {
                    /*do nothing*/
                }
            }
        }
    }

    /**
     * Levels supported by this logger.
     */
    enum class Level {

        /**
         * Default setting. We print all information about processing.
         * It includes errors, warnings, notifications, debug messages, requests and responses.
         */
        DEBUG,

        /**
         * This setting allows you to minimize information and print only errors, warnings.
         */
        WARN,

        /**
         * Disable debug-logs.
         */
        NONE
    }
}