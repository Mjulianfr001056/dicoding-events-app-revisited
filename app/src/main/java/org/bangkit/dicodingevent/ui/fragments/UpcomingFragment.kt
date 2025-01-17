package org.bangkit.dicodingevent.ui.fragments

//import org.bangkit.dicodingevent.data.response.DicodingEvent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.databinding.FragmentUpcomingBinding
import org.bangkit.dicodingevent.ui.activities.DetailActivity
import org.bangkit.dicodingevent.ui.DicodingEventAdapter
import org.bangkit.dicodingevent.ui.viewmodels.MainViewModel

class UpcomingFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val bottomNavigationView: View by lazy {
        requireActivity().findViewById(R.id.nav_view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUpcomingBinding.bind(view)

        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = DicodingEventAdapter { dicodingEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, dicodingEvent.id)
            startActivity(intent)
        }
        binding.rvEvent.adapter = adapter


        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireActivity())
        val searchAdapter = DicodingEventAdapter { dicodingEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, dicodingEvent.id)
            startActivity(intent)
        }
        binding.rvSearchResults.adapter = searchAdapter

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, _, _ ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    viewModel.searchEvent(textView.text.toString())
                    false
                }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.searchBar.clearText()
                viewModel.clearSearchResults()
            }
        })


        lifecycleScope.launch {
            viewModel.upcomingEventList.collectLatest { eventList ->
                setEventList(eventList, adapter)
                Log.d(TAG, "Fetched upcoming events: ${eventList.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                showLoading(isLoading, binding)
                Log.d(TAG, "Loading state: $isLoading")
            }
        }

        lifecycleScope.launch {
            viewModel.searchedEventList.collectLatest { filteredEvents ->
                if (filteredEvents.isNotEmpty()) {
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.rvEvent.visibility = View.GONE
                    searchAdapter.submitList(filteredEvents)
                    bottomNavigationView.visibility = View.GONE
                } else {
                    binding.rvSearchResults.visibility = View.GONE
                    binding.rvEvent.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                Log.d(TAG, "Fetched searched events: ${filteredEvents.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessages.collectLatest { errorMessage ->
                showErrorSnackbar(errorMessage, binding.root)
            }
        }
    }

    private fun setEventList(eventList : List<DicodingEventModel>, adapter: DicodingEventAdapter) {
        adapter.submitList(eventList)
    }

    private fun showErrorSnackbar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean, binding: FragmentUpcomingBinding) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "UpcomingFragment"
    }
}