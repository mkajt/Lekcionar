package si.kapucini.lekcionar.model.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "LEKCIONAR_DATASTORE")

class DataStoreManager(context: Context) {

    companion object {
        val updatedDataTimestamp = longPreferencesKey("UPDATED_DATA_TIMESTAMP")
        val firstDataTimestamp = longPreferencesKey("FIRST_DATA_TIMESTAMP")
        val lastDataTimestamp = longPreferencesKey("LAST_DATA_TIMESTAMP")
        val isDarkTheme = booleanPreferencesKey("IS_DARK_THEME")
        val red = stringPreferencesKey("RED")
        val skofija = stringPreferencesKey("SKOFIJA")
        val testUpdate = longPreferencesKey("TEST_UPDATE") //TODO delete before release
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
                val timestamp = preferences[updatedDataTimestamp] ?: 0L
                timestamp
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
                val first = preferences[firstDataTimestamp] ?: 0L
                first
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
                val last = preferences[lastDataTimestamp] ?: 0L
                last
            }
    }

    suspend fun setRed(selectedRed: String) {
        dataStore.edit { preferences ->
            preferences[red] = selectedRed
        }
    }

    fun getRed(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val red = preferences[red] ?: "noben"
                red
            }
    }

    suspend fun setSkofija(selectedSkofija: String) {
        dataStore.edit { preferences ->
            preferences[skofija] = selectedSkofija
        }
    }

    fun getSkofija(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val skofija = preferences[skofija] ?: "slovenija"
                skofija
            }
    }

    suspend fun setTestUpdateTimestamp(timestamp: Long) { //TODO delete
        dataStore.edit { preferences ->
            preferences[testUpdate] = timestamp
        }
    }

    fun getTestUpdateTimestamp(): Flow<Long> {  //TODO delete
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val timestamp = preferences[testUpdate] ?: 0L
                timestamp
            }
    }
}