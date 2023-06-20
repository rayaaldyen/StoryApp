package com.example.storyapp.ui.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.remote.story.GetStoriesResponse
import com.example.storyapp.data.remote.story.StoryModel
import com.example.storyapp.preference.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val _story = MutableLiveData<ArrayList<StoryModel>>()
    val story: LiveData<ArrayList<StoryModel>> = _story


    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        getAllStories()
    }


    fun getAllStories() {
        _isLoading.value = true
        _error.value = false
        val token = UserPreference(context).getUser().token
        val client = token?.let { tkn ->
            ApiConfig.getApiService().getALlStories(token = "Bearer $tkn", page = 1, size = 100, location = 1)
        }
        client?.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                if (response.isSuccessful && response.body()?.error == false) {
                    _story.value = response.body()?.listStory
                    _isLoading.value = false
                    _error.value = false
                } else {
                    Log.e(TAG, "\"onFailure: ${response.message()}\"")
                    _error.value = true
                }
            }

            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

}