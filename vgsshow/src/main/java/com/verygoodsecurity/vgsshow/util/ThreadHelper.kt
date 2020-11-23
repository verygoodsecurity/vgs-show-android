package com.verygoodsecurity.vgsshow.util

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal object ThreadHelper {

    private val executor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    @AnyThread
    fun runOnBackgroundThread(action: Runnable): Future<*> = executor.submit(action)

    @AnyThread
    fun runOnUiThread(action: Runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.run()
        }
    }
}