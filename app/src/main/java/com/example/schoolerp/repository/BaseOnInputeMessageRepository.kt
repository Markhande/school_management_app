package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.BaseOnInputMessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseOnInputeMessageRepository(private val apiService: ApiService) {

    fun fetchBaseOnInputMessage(inputMap: Map<String, String>): LiveData<Result<BaseOnInputMessageResponse>> {
        val liveData = MutableLiveData<Result<BaseOnInputMessageResponse>>()

        // Call the API and post the result
        apiService.baseOnInputeMessage(inputMap).enqueue(object : Callback<BaseOnInputMessageResponse> {
            override fun onResponse(
                call: Call<BaseOnInputMessageResponse>,
                response: Response<BaseOnInputMessageResponse>
            ) {
                if (response.isSuccessful) {
                    // Post the successful result without casting
                    liveData.postValue(Result.success(response.body()!!))
                } else {
                    // Post failure result
                    liveData.postValue(Result.failure(Throwable("Error fetching data")))
                }
            }

            override fun onFailure(call: Call<BaseOnInputMessageResponse>, t: Throwable) {
                // Post failure result
                liveData.postValue(Result.failure(t))
            }
        })
        return liveData
    }
}
