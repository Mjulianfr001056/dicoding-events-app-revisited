package org.bangkit.dicodingevent.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.databinding.FragmentFavoriteBinding
import org.bangkit.dicodingevent.databinding.FragmentFinishedBinding
import org.bangkit.dicodingevent.ui.DetailActivity
import org.bangkit.dicodingevent.ui.DicodingEventAdapter
import org.bangkit.dicodingevent.ui.MainActivity
import org.bangkit.dicodingevent.ui.viewmodels.MainViewModel

class FavoriteFragment : Fragment() {

    private lateinit var binding : FragmentFavoriteBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = DicodingEventAdapter { dicodingEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, dicodingEvent.id)
            startActivity(intent)
        }
        binding.rvEvent.adapter = adapter

        with(activity as MainActivity) {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.title = getString(R.string.favorite_events_title)
            binding.toolbar.setTitleTextColor(resources.getColor(android.R.color.white, null))
        }

        lifecycleScope.launch {
            viewModel.favoriteEventList.collectLatest { eventList ->
                setEventList(eventList, adapter)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessages.collectLatest { message ->
                showErrorSnackbar(message, binding.root)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                showLoading(isLoading, binding)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForFavoriteEventsUpdate()
        Log.d(TAG, "onResume: ${viewModel.favoriteEventList.value.size}")
    }

    private fun setEventList(eventList: List<DicodingEventModel>, adapter: DicodingEventAdapter) {
        adapter.submitList(eventList)
    }

    private fun showErrorSnackbar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean, binding: FragmentFavoriteBinding) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "FavoriteFragment"
    }
}