package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.UpatedFeesCollectionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpatedFeesCollectionRepository(private val apiService: ApiService) {

    fun updateFeesCollection(feesCollectionData: Map<String, String>): LiveData<Result<UpatedFeesCollectionResponse>> {
        val result = MutableLiveData<Result<UpatedFeesCollectionResponse>>()

        // Make the API call to update fees collection
        apiService.UpateedFeesCollection(feesCollectionData).enqueue(object :
            Callback<UpatedFeesCollectionResponse> {
            override fun onResponse(
                call: Call<UpatedFeesCollectionResponse>,
                response: Response<UpatedFeesCollectionResponse>
            ) {
                if (response.isSuccessful) {
                    // If the response is successful, post the result to LiveData
                    response.body()?.let {
                        result.postValue(Result.success(it))
                    } ?: result.postValue(Result.failure(Exception("Response body is null")))
                } else {
                    // If the response is not successful, post the failure message
                    result.postValue(Result.failure(Exception("API Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<UpatedFeesCollectionResponse>, t: Throwable) {
                // If the network call fails, post the error to LiveData
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}