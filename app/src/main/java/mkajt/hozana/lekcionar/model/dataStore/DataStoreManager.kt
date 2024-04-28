package mkajt.hozana.lekcionar.model.dataStore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "LEKCIONAR_DATASTORE")

class DataStoreManager(context: Context) {

    companion object {
        val updatedDataTimestamp = longPreferencesKey("UPDATED_DATA_TIMESTAMP")
        val firstDataTimestamp = longPreferencesKey("FIRST_DATA_TIMESTAMP")
        val lastDataTimestamp = longPreferencesKey("LAST_DATA_TIMESTAMP")
        val isDarkTheme = booleanPreferencesKey("IS_DARK_THEME")
    }

    private val dataStore = context.dataStore

    suspend fun setTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[isDarkTheme] = isDark
        }
    }

    fun getTheme(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("DataStore", exception.message.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val uiMode = preferences[isDarkTheme] ?: false
                uiMode

            }
    }

    suspend fun setUpdatedDataTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[updatedDataTimestamp] = timestamp
        }
    }

    fun getUpdatedDataTimestamp(): Flow<Long> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val uiMode = preferences[updatedDataTimestamp] ?: 0L
                uiMode

            }
    }

    suspend fun setFirstDataTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[firstDataTimestamp] = timestamp
        }
    }

    fun getFirstDataTimestamp(): Flow<Long> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val uiMode = preferences[firstDataTimestamp] ?: 0L
                uiMode

            }
    }

    suspend fun setLastDataTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[lastDataTimestamp] = timestamp
        }
    }

    fun getLastDataTimestamp(): Flow<Long> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val uiMode = preferences[lastDataTimestamp] ?: 0L
                uiMode

            }
    }
}