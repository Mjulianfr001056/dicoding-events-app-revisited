package org.bangkit.dicodingevent.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bangkit.dicodingevent.data.repository.DicodingEvent
import org.bangkit.dicodingevent.databinding.ActivityDetailBinding
import org.bangkit.dicodingevent.ui.viewmodels.DetailActivityViewModel

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

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
        binding.toolbar.navigationIcon.let {
            it?.setTint(resources.getColor(android.R.color.white, theme))
        }

        eventId = intent.getIntExtra(EXTRA_EVENT, -1)
        if (eventId == -1) {
            return
        }

        viewModel.setEvent(eventId)

        lifecycleScope.launch {
            viewModel.event.collectLatest { event ->
                event?.let {
                    setupView( event )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                showLoading(isLoading, binding)
            }
        }
    }

    private fun setupView(event : DicodingEvent) {
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