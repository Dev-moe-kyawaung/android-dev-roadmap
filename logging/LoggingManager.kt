// logging/LoggingManager.kt
package com.yourapp.core.logging

import android.util.Log
import timber.log.Timber

class LoggingTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "[%s:%s:%s]",
            super.createStackElementTag(element),
            element.methodName,
            element.lineNumber
        )
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val formattedMessage = "[${getTimestamp()}] $message"
        super.log(priority, tag, formattedMessage, t)

        // Also log to file in production
        if (priority >= Log.WARN) {
            logToFile(priority, tag, formattedMessage, t)
        }
    }

    private fun getTimestamp(): String {
        return java.text.SimpleDateFormat(
            "HH:mm:ss.SSS",
            java.util.Locale.US
        ).format(System.currentTimeMillis())
    }

    private fun logToFile(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        // Implementation for file logging
    }
}

object LoggingInitializer {
    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(LoggingTree())
        }
    }
}

