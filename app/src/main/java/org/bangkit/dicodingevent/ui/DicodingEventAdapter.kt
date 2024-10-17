package org.bangkit.dicodingevent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.databinding.ItemEventBinding

class DicodingEventAdapter(
    private val onClickItemListener : (DicodingEventModel) -> Unit
) : ListAdapter<DicodingEventModel, DicodingEventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClickItemListener)
    }

    class MyViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: DicodingEventModel, onItemClickListener: (DicodingEventModel) -> Unit) {
            Glide.with(itemView.context)
                .load(event.mediaCover)
                .into(binding.ivEventPicture)
            binding.tvEventTitle.text = event.name

            itemView.setOnClickListener {
                onItemClickListener(event)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DicodingEventModel>() {
            override fun areItemsTheSame(oldItem: DicodingEventModel, newItem: DicodingEventModel): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: DicodingEventModel, newItem: DicodingEventModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}