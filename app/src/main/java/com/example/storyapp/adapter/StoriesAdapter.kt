package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.paging.PagingDataAdapter
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.databinding.ItemListBinding
import com.example.storyapp.utils.withDateFormat

class StoriesAdapter:
    PagingDataAdapter<StoryModel, StoriesAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    class ListViewHolder(var binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val stories = getItem(position)

        holder.binding.apply {
            if (stories != null) {
                Glide.with(holder.itemView.context)
                    .load(stories.photoUrl)
                    .into(storyPhoto)

                tvUsersName.text = stories.name
                tvDate.text = stories.createdAt.withDateFormat()
                tvDesc.text = stories.description

            }
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(stories)
            }
        }


    }


    interface OnItemClickCallback {
        fun onItemClicked(story: StoryModel?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
            }
        }


    }
}