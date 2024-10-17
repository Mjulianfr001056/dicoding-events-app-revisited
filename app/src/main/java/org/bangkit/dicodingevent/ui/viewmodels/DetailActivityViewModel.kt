package org.bangkit.dicodingevent.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.data.repository.DicodingEvent
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

@HiltViewModel
class DetailActivityViewModel @Inject constructor(
    private val repository : DicodingEventRepository
) : ViewModel() {

    private val _event = MutableStateFlow<DicodingEvent?>(null)
    val event = _event.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setEvent(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEventDetail(eventId).let { event ->
                when (event) {
                    is Result.Success -> {
                        _event.value = event.data
                    }
                    is Result.Error -> {
                        Log.e(TAG, "setEvent: ${event.message}")
                    }
                }
            }
        }
        _isLoading.value = false
    }

    companion object {
        private const val TAG = "DetailActivityViewModel"
    }
}