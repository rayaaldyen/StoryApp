package com.example.storyapp.ui.setStory

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.R
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.remote.story.SetStoriesResponse
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SetStoryViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun setStory(image: File, descript: String, lat: Double, lon: Double) {
        _isLoading.value = true
        val reducedFile = reduceFileImage(image)

        val description = descript.toRequestBody("text/plain".toMediaType())
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            reducedFile.name,
            requestImageFile
        )
        val token = UserPreference(context).getUser().token
        val client = token?.let { tkn ->
            ApiConfig.getApiService().setStories(
                token = "Bearer $tkn",
                file = imageMultipart,
                description = description,
                lat = lat.toFloat(),
                lon = lon.toFloat()
            )
        }
        client?.enqueue(object : Callback<SetStoriesResponse> {
            override fun onResponse(
                call: Call<SetStoriesResponse>,
                response: Response<SetStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isLoading.value = false
                        _error.value = false
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SetStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = true
                Toast.makeText(
                    context,
                    context.getString(R.string.failed_retrofit),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

}