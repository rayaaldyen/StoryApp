package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.preference.UserPreference

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val pref: UserPreference
) : RemoteMediator<Int, StoryModel>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryModel>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX
        val token = pref.getUser().token.toString()
        return try {
            val responseData = token.let { tkn ->
                apiService.getStories("Bearer $tkn", page, state.config.pageSize, 0) }
            if (responseData.isSuccessful) {
                val endOfPaginationReached = responseData.body()!!.listStory.isEmpty()
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(Exception("Error while loading stories"))
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }


    }


}