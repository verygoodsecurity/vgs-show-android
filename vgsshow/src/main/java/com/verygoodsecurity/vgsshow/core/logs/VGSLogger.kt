package com.verygoodsecurity.vgsshow.core.logs

import android.util.Log

object VGSLogger {

    private const val TAG = "VGSShow"

    /**
     * Current log level.
     */
    var level: Level = Level.DEBUG

    /**
     * Current log level.
     */
    var isNetworkLogsEnabled: Boolean = true

    /**
     *
     * @return true if the logger is logging at DEBUG level or above.
     */
    fun isDebugEnabled() = Level.DEBUG.ordinal >= level.ordinal

    internal fun debug(tag: String, message: String, vararg values: Any?) {
        log(Level.DEBUG, tag, message, *values)
    }

    internal fun warning(tag: String, message: String, vararg values: Any?) {
        log(Level.WARN, tag, message, *values)
    }

    internal fun network(tag: String, message: String, vararg values: Any?) {
        if (isNetworkLogsEnabled) {
            log(Level.DEBUG, tag, message, *values)
        }
    }

    private fun log(level: Level, tag: String, message: String, vararg values: Any?) {
        if (level.ordinal >= this.level.ordinal) {
            val msg = "$tag : ${String.format(message, *values)}"
            when (level) {
                Level.DEBUG -> Log.i(TAG, msg)
                Level.WARN -> Log.w(TAG, msg)
                Level.NONE -> {
                    /*do nothing*/
                }
            }
        }
    }

    /**
     * Level supported by this logger.
     */
    enum class Level {

        DEBUG,
        WARN,
        NONE
    }
}