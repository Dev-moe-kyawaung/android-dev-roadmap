package com.yourapp.core.analytics

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun logEvent(
        eventName: String,
        parameters: Map<String, Any> = emptyMap()
    ) {
        try {
            val bundle = android.os.Bundle().apply {
                parameters.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
            firebaseAnalytics.logEvent(eventName, bundle)
            Timber.d("Analytics event logged: $eventName")
        } catch (e: Exception) {
            Timber.e(e, "Error logging analytics event")
        }
    }

    fun logScreenView(screenName: String) {
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, mapOf(
            FirebaseAnalytics.Param.SCREEN_NAME to screenName,
            FirebaseAnalytics.Param.SCREEN_CLASS to screenName
        ))
    }

    fun logPurchase(
        itemId: String,
        itemName: String,
        price: Double,
        currency: String = "USD"
    ) {
        logEvent(FirebaseAnalytics.Event.PURCHASE, mapOf(
            FirebaseAnalytics.Param.ITEM_ID to itemId,
            FirebaseAnalytics.Param.ITEM_NAME to itemName,
            FirebaseAnalytics.Param.VALUE to price,
            FirebaseAnalytics.Param.CURRENCY to currency
        ))
    }

    fun logError(errorName: String, errorDetails: String? = null) {
        logEvent("app_error", mapOf(
            "error_name" to errorName,
            "error_details" to (errorDetails ?: "Unknown")
        ))
    }

    fun setUserProperty(propertyName: String, value: String) {
        firebaseAnalytics.setUserProperty(propertyName, value)
    }
}

