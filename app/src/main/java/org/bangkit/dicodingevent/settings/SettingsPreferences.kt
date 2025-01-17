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
    private val themeKey = booleanPreferencesKey("theme_setting")
    private val dailyReminderKey = booleanPreferencesKey("daily_reminder_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    suspend fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[dailyReminderKey] = isDailyReminderActive
        }
    }
}