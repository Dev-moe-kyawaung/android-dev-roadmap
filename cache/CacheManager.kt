package com.yourapp.core.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_cache")

@Singleton
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    suspend fun <T> setCache(
        key: String,
        value: String,
        ttlMinutes: Long = 60
    ) {
        try {
            val timestampKey = stringPreferencesKey("${key}_timestamp")
            val valueKey = stringPreferencesKey(key)
            
            dataStore.edit { preferences ->
                preferences[valueKey] = value
                preferences[timestampKey] = System.currentTimeMillis().toString()
            }
            Timber.d("Cache set: $key (TTL: $ttlMinutes minutes)")
        } catch (e: Exception) {
            Timber.e(e, "Error setting cache")
        }
    }

    fun <T> getCache(key: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            try {
                val valueKey = stringPreferencesKey(key)
                val timestampKey = stringPreferencesKey("${key}_timestamp")
                
                val value = preferences[valueKey]
                val timestamp = preferences[timestampKey]?.toLongOrNull() ?: 0L
                
                val isExpired = System.currentTimeMillis() - timestamp > TimeUnit.HOURS.toMillis(1)
                
                if (isExpired) {
                    clearCache(key)
                    null
                } else {
                    value
                }
            } catch (e: Exception) {
                Timber.e(e, "Error getting cache")
                null
            }
        }
    }

    suspend fun clearCache(key: String) {
        try {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
                preferences.remove(stringPreferencesKey("${key}_timestamp"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error clearing cache")
        }
    }

    suspend fun clearAllCache() {
        try {
            dataStore.edit { it.clear() }
            Timber.d("All cache cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing all cache")
        }
    }
}

