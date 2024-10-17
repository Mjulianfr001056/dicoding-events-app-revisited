package org.bangkit.dicodingevent.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

@HiltViewModel
class DetailActivityViewModel @Inject constructor(
    private val repository : DicodingEventRepository
) : ViewModel() {

    private val _event = MutableStateFlow(DicodingEventModel())
    val event = _event.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorMessages = _errorChannel.receiveAsFlow()

    fun setEvent(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEventDetail(eventId).let { event ->
                when (event) {
                    is Result.Success -> handleSuccess(event.data, _event)
                    is Result.Error -> {
                        handleError(event.message)
                    }
                }
            }
        }
        _isLoading.value = false
    }

    private suspend fun handleError(errorMessage: String?) {
        val error = errorMessage ?: "Terjadi kesalahan"
        Log.e(TAG, "Error: $error")
        _errorChannel.send(error)
    }

    private fun handleSuccess(data: DicodingEventModel?, targetFlow: MutableStateFlow<DicodingEventModel>) {
        data?.let {
            targetFlow.value = it
        }
    }

    companion object {
        private const val TAG = "DetailActivityViewModel"
    }
}