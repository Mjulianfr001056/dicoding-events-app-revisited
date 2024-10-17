package org.bangkit.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.data.repository.DicodingEvent
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DicodingEventRepository
) : ViewModel() {
    private val _upcomingEventList = MutableStateFlow<List<DicodingEvent>>(emptyList())
    val upcomingEventList = _upcomingEventList.asStateFlow()

    private val _finishedEventList = MutableStateFlow<List<DicodingEvent>>(emptyList())
    val finishedEventList = _finishedEventList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        Log.d(TAG, "MainViewModel: Initialized")
        getEvents()
    }

    private fun getEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEvents(isActive = true).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        result.message?.let {
                            Log.d(TAG, "Error: $it")
                        }
                    }
                    is Result.Success -> {
                        if(result.data == null) {
                            Log.d(TAG, "Data is null")
                        } else {
                            if (result.data.isNotEmpty()) {
                                _upcomingEventList.value = result.data
                            }
                        }
                    }
                }
            }

            repository.getEvents(isActive = false).collectLatest { result ->
                _isLoading.value = false
                when (result) {
                    is Result.Error -> {
                        result.message?.let {
                            Log.d(TAG, "Error: $it")
                        }
                    }
                    is Result.Success -> {
                        if(result.data == null) {
                            Log.d(TAG, "Data is null")
                        } else {
                            if (result.data.isNotEmpty()) {
                                _finishedEventList.value = result.data
                            }
                        }
                    }
                }
            }
        }
    }
}