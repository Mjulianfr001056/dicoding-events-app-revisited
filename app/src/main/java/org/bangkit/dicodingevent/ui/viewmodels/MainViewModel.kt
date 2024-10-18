package org.bangkit.dicodingevent.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.settings.SettingPreferences
import org.bangkit.dicodingevent.util.Result
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DicodingEventRepository,
    private val settingsPreference: SettingPreferences
) : ViewModel() {
    private val _upcomingEventList = MutableStateFlow<List<DicodingEventModel>>(emptyList())
    val upcomingEventList = _upcomingEventList.asStateFlow()

    private val _finishedEventList = MutableStateFlow<List<DicodingEventModel>>(emptyList())
    val finishedEventList = _finishedEventList.asStateFlow()

    private val _searchedEventList = MutableStateFlow<List<DicodingEventModel>>(emptyList())
    val searchedEventList = _searchedEventList.asStateFlow()

    private val _favoriteEventList = MutableStateFlow<List<DicodingEventModel>>(emptyList())
    val favoriteEventList = _favoriteEventList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorMessages = _errorChannel.receiveAsFlow()

    init {
        Log.d(TAG, "MainViewModel: Initialized")
        fetchAllEvents()
    }

    fun getThemeSettings() = settingsPreference.getThemeSetting()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            settingsPreference.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        viewModelScope.launch {
            settingsPreference.saveDailyReminderSetting(isDailyReminderActive)
        }
    }

    fun searchEvent(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.searchEvent(query).collectLatest { result ->
                when (result) {
                    is Result.Error -> handleError(result.message)
                    is Result.Success -> handleSuccess(result.data, _searchedEventList)
                }
            }
            Log.d(TAG, "_searchedEventList: ${_searchedEventList.value.size}")
        }
        _isLoading.value = false
    }

    fun checkForFavoriteEventsUpdate() {
        viewModelScope.launch {
            fetchFavoriteEvents()
        }
    }

    private fun fetchAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchUpcomingEvents()
                fetchFinishedEvents()
                fetchFavoriteEvents()
            } catch (e: IOException) {
                Log.e(TAG, "Network error: ${e.message}")
                _errorChannel.send("Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mendapatkan data: ${e.message}")
                _errorChannel.send("Gagal mendapatkan data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUpcomingEvents() {
        repository.getEvents(isActive = true).collectLatest { result ->
            when (result) {
                is Result.Error -> handleError(result.message)
                is Result.Success -> handleSuccess(result.data, _upcomingEventList)
            }
        }
    }

    private suspend fun fetchFinishedEvents() {
        repository.getEvents(isActive = false).collectLatest { result ->
            when (result) {
                is Result.Error -> handleError(result.message)
                is Result.Success -> handleSuccess(result.data, _finishedEventList)
            }
        }
    }

    private suspend fun fetchFavoriteEvents() {
        repository.getAllFavoriteEvents().collectLatest { result ->
            when (result) {
                is Result.Error -> handleError(result.message)
                is Result.Success -> handleSuccess(result.data, _favoriteEventList)
            }
        }
    }

    private suspend fun handleError(errorMessage: String?) {
        val error = errorMessage ?: "Terjadi kesalahan"
        Log.e(TAG, "Error: $error")
        _errorChannel.send(error)
    }

    private fun handleSuccess(data: List<DicodingEventModel>?, targetFlow: MutableStateFlow<List<DicodingEventModel>>) {
        if (data.isNullOrEmpty()) {
            Log.d(TAG, "Data null atau kosong")
        } else {
            targetFlow.value = data
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}