package com.verygoodsecurity.vgsshow.core.logs

import android.util.Log
import com.verygoodsecurity.vgsshow.BuildConfig

/**
 * This object is used to log messages in VGS Show SDK.
 */
object VGSShowLogger {

    private const val TAG = "VGSShow"

    /**
     * Current log level.
     */
    var level: Level = if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE

    /**
     *
     * @return true if the logger is logging at DEBUG level.
     */
    fun isDebugEnabled() = Level.DEBUG.ordinal >= level.ordinal

    internal fun debug(tag: String, message: String) {
        log(Level.DEBUG, tag, message)
    }

    internal fun warning(tag: String, message: String) {
        log(Level.WARN, tag, message)
    }

    private fun log(level: Level, tag: String, message: String?) {
        if (level.ordinal >= this.level.ordinal) {
            val msg = "$tag : $message"
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
     * Level supported by this logger.
     */
    enum class Level {

        DEBUG,
        WARN,
        NONE
    }
}