package org.bangkit.dicodingevent.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.ui.viewmodels.MainViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    val viewModel: MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val themeSettingPreference = findPreference<SwitchPreferenceCompat>("dark_mode")
        themeSettingPreference?.setOnPreferenceChangeListener { _, newValue ->
            Log.d(TAG, "onCreatePreferences: $newValue")
            val isDarkModeActive = newValue as Boolean

            viewModel.saveThemeSetting(isDarkModeActive)
            updateThemeIcon(isDarkModeActive)
            applyTheme(isDarkModeActive)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.getThemeSettings().collectLatest { isDarkModeActive ->
                updateThemeIcon(isDarkModeActive)
            }
        }
    }

    private fun updateThemeIcon(isDarkModeActive: Boolean) {
        val themeSettingPreference = findPreference<Preference>("dark_mode")

        themeSettingPreference?.icon?.let { icon ->
            val color = if (isDarkModeActive) {
                resources.getColor(android.R.color.white, null)
            } else {
                resources.getColor(android.R.color.black, null)
            }

            icon.setTint(color)
        }
    }

    private fun applyTheme(isDarkModeActive: Boolean) {
        Log.d(TAG, "applyTheme: $isDarkModeActive")
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}