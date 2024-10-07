package org.bangkit.dicodingevent.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.response.DicodingEvent
import org.bangkit.dicodingevent.databinding.ActivityMainBinding
import org.bangkit.dicodingevent.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val layoutManager = LinearLayoutManager(this)
        binding.rvEvent.layoutManager = layoutManager

        viewModel.eventList.observe(this) {eventList ->
            setEventList(eventList)
        }

        viewModel.isLoading.observe(this) {isLoading ->
            showLoading(isLoading)
        }
    }

    private fun setEventList(eventList : List<DicodingEvent>) {
//        val adapter = DicodingEventAdapter()
//        adapter.submitList(eventList)
//        binding.rvEvent.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}