package org.bangkit.dicodingevent.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.repository.DicodingEvent
import org.bangkit.dicodingevent.databinding.FragmentHomeBinding
import org.bangkit.dicodingevent.ui.DetailActivity
import org.bangkit.dicodingevent.ui.DicodingEventAdapter
import org.bangkit.dicodingevent.ui.DicodingHomeEventAdapter
import org.bangkit.dicodingevent.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        binding.upcomingEventsRecycler.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        val upcomingEventAdapter = DicodingHomeEventAdapter { dicodingEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, dicodingEvent.id)
            startActivity(intent)
        }

        binding.upcomingEventsRecycler.adapter = upcomingEventAdapter

        binding.finishedEventsRecycler.layoutManager = LinearLayoutManager(requireActivity())

        val finishedEventAdapter = DicodingEventAdapter { dicodingEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, dicodingEvent.id)
            startActivity(intent)
        }

        binding.finishedEventsRecycler.adapter = finishedEventAdapter

        lifecycleScope.launch {
            viewModel.upcomingEventList.collectLatest { eventList ->
                setUpcomingEventList(eventList, upcomingEventAdapter)
                Log.d("HomeFragment", "Fetched upcoming events: ${eventList.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.finishedEventList.collectLatest { eventList ->
                setFinishedEventList(eventList, finishedEventAdapter)
                Log.d("HomeFragment", "Fetched finished events: ${eventList.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                showLoading(isLoading, binding)
                Log.d("HomeFragment", "Loading state: $isLoading")
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessages.collectLatest { errorMessage ->
                showErrorSnackbar(errorMessage, binding.root)
            }
        }
    }

    private fun setUpcomingEventList(eventList : List<DicodingEvent>, adapter: DicodingHomeEventAdapter) {
        adapter.submitList(eventList)
    }

    private fun setFinishedEventList(eventList : List<DicodingEvent>, adapter: DicodingEventAdapter) {
        adapter.submitList(eventList)
    }

    private fun showLoading(isLoading: Boolean, binding: FragmentHomeBinding) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorSnackbar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}