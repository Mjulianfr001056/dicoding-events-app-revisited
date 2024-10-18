package org.bangkit.dicodingevent.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    fun getDailyReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DAILY_REMINDER_KEY] ?: false
        }
    }

    suspend fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_KEY] = isDailyReminderActive
        }
    }
}