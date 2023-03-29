package com.example.debugger

import android.util.Log

object MyLog {

    private const val DEFAULT_LOG_TAG = "QQQQ"

    fun log(
        log: String,
        logLevel: Int = Log.VERBOSE,
        tag: String = DEFAULT_LOG_TAG,
        throwable: Throwable? = null
    ) {
        if (BuildConfig.DEBUG) {
            when (logLevel) {
                Log.VERBOSE -> {
                    Log.v(tag, log, throwable)
                }
                Log.DEBUG -> {
                    Log.d(tag, log, throwable)
                }
                Log.INFO -> {
                    Log.i(tag, log, throwable)
                }
                Log.WARN -> {
                    Log.w(tag, log, throwable)
                }
                Log.ERROR -> {
                    Log.e(tag, log, throwable)
                }
                Log.ASSERT -> {
                    Log.wtf(tag, log, throwable)
                }
            }
        }
    }
}