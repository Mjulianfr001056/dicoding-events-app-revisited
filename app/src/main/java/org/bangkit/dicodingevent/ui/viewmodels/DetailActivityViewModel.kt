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

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _messageChannel = Channel<String>()
    val messages = _messageChannel.receiveAsFlow()

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
        if (_isFavorite.value) {
            setFavorite()
        } else {
            removeFavorite()
        }
    }

    fun setEvent(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEventDetail(eventId).let { event ->
                when (event) {
                    is Result.Success -> {
                        handleSuccess(event.data, _event)
                        isEventAlreadyFavorite()
                    }
                    is Result.Error -> {
                        handleError(event.message)
                    }
                }
            }
            _isLoading.value = false
        }
    }

    private fun isEventAlreadyFavorite() {
        viewModelScope.launch {
            val eventId = _event.value.id ?: return@launch
            val result = repository.findFavorite(eventId)
            _isFavorite.value = result is Result.Success
        }
    }


    private fun setFavorite() {
        viewModelScope.launch {
            val currentEvent = _event.value
            repository.addFavorite(currentEvent).collectLatest { event ->
                when (event) {
                    is Result.Success -> {
                        _messageChannel.send("Berhasil menambahkan ke favorit")
                        Log.d(TAG, "setFavorite: Berhasil menambahkan ${currentEvent.name} ke favorit")
                    }
                    is Result.Error -> {
                        handleError(event.message)
                    }
                }
            }
        }
    }

    private fun removeFavorite() {
        viewModelScope.launch {
            val currentEvent = _event.value
            repository.removeFavorite(currentEvent).collectLatest { event ->
                when (event) {
                    is Result.Success -> {
                        _messageChannel.send("Berhasil menghapus dari favorit")
                        Log.d(TAG, "removeFavorite: Berhasil menghapus ${currentEvent.name} dari favorit")
                    }
                    is Result.Error -> {
                        handleError(event.message)
                    }
                }
            }
        }
    }

    private suspend fun handleError(errorMessage: String?) {
        val error = errorMessage ?: "Terjadi kesalahan"
        Log.e(TAG, "Error: $error")
        _messageChannel.send(error)
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