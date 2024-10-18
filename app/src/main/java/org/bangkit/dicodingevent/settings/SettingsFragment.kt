package org.bangkit.dicodingevent.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.ui.viewmodels.MainViewModel
import org.bangkit.dicodingevent.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var workManager: WorkManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            applyDailyReminder()
        } else {
            Snackbar.make(requireView(), "Izin ditolak, pengingat tidak diaktifkan", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initThemeSetting()
        initDailyReminderSetting()
    }

    private fun initThemeSetting() {
        findPreference<SwitchPreferenceCompat>("dark_mode")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val isDarkModeActive = newValue as Boolean
                viewModel.saveThemeSetting(isDarkModeActive)
                updateIcons(isDarkModeActive)
                applyTheme(isDarkModeActive)
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireContext())

        lifecycleScope.launch {
            viewModel.getThemeSettings().collectLatest { isDarkModeActive ->
                updateIcons(isDarkModeActive)
            }
        }
    }

    private fun initDailyReminderSetting() {
        findPreference<SwitchPreferenceCompat>("daily_reminder")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val isDailyReminderActive = newValue as Boolean

                if (isDailyReminderActive) {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        applyDailyReminder()
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= 33) {
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                        return@setOnPreferenceChangeListener false
                    }
                } else {
                    cancelDailyReminder()
                }

                viewModel.saveDailyReminderSetting(isDailyReminderActive)
                true
            }
        }
    }

    private fun updateIcons(isDarkModeActive: Boolean) {
        val color = if (isDarkModeActive) {
            resources.getColor(android.R.color.white, null)
        } else {
            resources.getColor(android.R.color.black, null)
        }

        findPreference<Preference>("dark_mode")?.icon?.setTint(color)
        findPreference<Preference>("daily_reminder")?.icon?.setTint(color)
    }

    private fun applyTheme(isDarkModeActive: Boolean) {
        Log.d(TAG, "applyTheme: $isDarkModeActive")
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun applyDailyReminder() {
        Log.d(TAG, "applyDailyReminder: true")
        val periodicWorkRequest = PeriodicWorkRequest.Builder(DailyReminderWorker::class.java, 1, TimeUnit.DAYS)
            .addTag("daily_reminder")
            .build()

        workManager.enqueue(periodicWorkRequest)
    }

    private fun cancelDailyReminder() {
        workManager.cancelAllWorkByTag("daily_reminder")
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}