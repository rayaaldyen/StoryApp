package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.preference.UserPreference

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference(context)
        return StoryRepository.getInstance(apiService, pref)
    }
}