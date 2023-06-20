package com.example.storyapp.data.remote.story

import com.google.gson.annotations.SerializedName

data class SetStoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)
