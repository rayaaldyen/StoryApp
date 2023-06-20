package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.preference.UserPreference

class StoryRepository(private val apiService: ApiService, private val pref: UserPreference ) {
    fun getStoryList(): LiveData<PagingData<StoryModel>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(apiService, pref),
            pagingSourceFactory = {
                StoryPagingSource(pref)
            }
        ).flow.asLiveData()
    }

    fun logout(){
        pref.logout()
    }
    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService, pref)
                INSTANCE = instance
                instance
            }
        }
    }

}