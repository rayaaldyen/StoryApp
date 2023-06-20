package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val DETAIL = "detail"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val detailStory = intent.getParcelableExtra<StoryModel>(DETAIL) as StoryModel
        setContent(detailStory)
    }

    private fun setContent(detailStory: StoryModel) {
        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .into(imageStory)
            tvName.text = detailStory.name
            tvDate.text = detailStory.createdAt.withDateFormat()
            tvDesc.text = detailStory.description
        }
    }
}