package org.bangkit.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bangkit.dicodingevent.data.repository.DicodingEventRepository
import org.bangkit.dicodingevent.data.response.DicodingEvent
import org.bangkit.dicodingevent.data.response.DicodingEventResponse
import org.bangkit.dicodingevent.data.response.ListEventsItem
import org.bangkit.dicodingevent.data.retrofit.NetworkModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Lazy<DicodingEventRepository>
) : ViewModel() {
    private val _upcomingEventList = MutableStateFlow<List<DicodingEvent>>(emptyList())
    val upcomingEventList = _upcomingEventList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        getEvents()
    }

    private fun getEvents() {


//        val client = NetworkModule.getApiService().getEvents(0)
//        client.enqueue(object : Callback<DicodingEventResponse> {
//            override fun onResponse(
//                call: Call<DicodingEventResponse>,
//                response: Response<DicodingEventResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        _eventList.value = response.body()?.listEvents
//                    }
//                }else {
//                    Log.d(TAG, "onResponse: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<DicodingEventResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
    }
}