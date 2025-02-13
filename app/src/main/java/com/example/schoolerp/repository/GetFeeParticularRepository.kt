package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.FeeParticularResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetFeeParticularRepository (private val apiService: ApiService) {
    fun getFeeParticulars(schoolId: String, className: String): LiveData<FeeParticularResponse?> {
        val data = MutableLiveData<FeeParticularResponse?>()
        apiService.getFees(schoolId, className).enqueue(object : Callback<FeeParticularResponse> {
            override fun onResponse(
                call: Call<FeeParticularResponse>,
                response: Response<FeeParticularResponse>
            ) {
                if (response.isSuccessful) {
                    data.postValue(response.body())
                } else {
                    data.postValue(null)
                }
            }

            override fun onFailure(call: Call<FeeParticularResponse>, t: Throwable) {
                data.postValue(null)
            }
        })
        return data
    }
}