package com.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.preference.UserPreference

class StoryPagingSource(private val pref: UserPreference) : PagingSource<Int, StoryModel>(){

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, StoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryModel> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUser().token.toString()
            if (token.isNotEmpty()) {
                val responseData = token.let { tkn ->
                    ApiConfig.getApiService().getStories(
                        "Bearer $tkn",
                        position,
                        params.loadSize,
                        0
                    )
                }

                if (responseData.isSuccessful) {
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error while loading stories"))
                }
            } else {
                LoadResult.Error(Exception("Token is empty"))
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}