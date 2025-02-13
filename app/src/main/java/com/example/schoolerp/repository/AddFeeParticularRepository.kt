package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.AddFeeParticularResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFeeParticularRepository(private val apiService: ApiService) {

    fun addFeeParticular(addFeeParticularData: Map<String, String>): LiveData<AddFeeParticularResponse> {
        val result = MutableLiveData<AddFeeParticularResponse>()

        apiService.AddFeeParticular(addFeeParticularData).enqueue(object :
            Callback<AddFeeParticularResponse> {
            override fun onResponse(
                call: Call<AddFeeParticularResponse>,
                response: Response<AddFeeParticularResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(null)
                }
            }

            override fun onFailure(call: Call<AddFeeParticularResponse>, t: Throwable) {
                result.postValue(null)
            }
        })

        return result
    }
}