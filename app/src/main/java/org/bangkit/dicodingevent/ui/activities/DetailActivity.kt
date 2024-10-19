package org.bangkit.dicodingevent.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.R
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.databinding.ActivityDetailBinding
import org.bangkit.dicodingevent.ui.viewmodels.DetailActivityViewModel

@AndroidEntryPoint
class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailActivityViewModel by viewModels()
    private var eventId: Int = -1

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        eventId = intent.getIntExtra(EXTRA_EVENT, -1)
        if (eventId == -1) {
            return
        }

        viewModel.setEvent(eventId)

        binding.cvFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        lifecycleScope.launch {
            viewModel.event.collectLatest { event ->
                setupView( event )
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                showLoading(isLoading, binding)
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collectLatest { isFavorite ->
                binding.ivFavorite.setImageResource(
                    if (isFavorite) R.drawable.ic_filled_favorite
                    else R.drawable.ic_outlined_favorite
                )
            }
        }

        lifecycleScope.launch {
            viewModel.messages.collectLatest { message ->
                showSnackBar(message, binding.root)
            }
        }

    }

    private fun setupView(event : DicodingEventModel) {
        binding.toolbar.title = event.name

        binding.tvName.text = event.name
        binding.tvSummary.text = event.summary
        binding.tvOwnerName.text = event.ownerName
        binding.tvBeginTime.text = event.beginTime

        Glide.with(this@DetailActivity)
            .load(event.mediaCover)
            .into(binding.ivMediaCover)

        val quota = event.quota ?: 0
        val registrants = event.registrants ?: 0
        binding.cQuota.text = (quota - registrants).toString()

        binding.cDeskripsi.text = HtmlCompat.fromHtml(
            event.description ?: "",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.btnRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean, binding: ActivityDetailBinding) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}