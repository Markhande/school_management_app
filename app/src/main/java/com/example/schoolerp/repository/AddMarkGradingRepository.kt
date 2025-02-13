package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.AddMarkGradingResponse
import com.example.schoolerp.models.responses.HomeWorkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMarkGradingRepository(private val apiService: ApiService) {
    fun addMarkGrading(MarkGradingData: Map<String, String>): LiveData<AddMarkGradingResponse> {
        val result = MutableLiveData<AddMarkGradingResponse>()

        apiService.addMarkGrading(MarkGradingData).enqueue(object : Callback<AddMarkGradingResponse> {
            override fun onResponse(call: Call<AddMarkGradingResponse>, response: Response<AddMarkGradingResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    result.value = response.body()
                } else {

                }
            }

            override fun onFailure(call: Call<AddMarkGradingResponse>, t: Throwable) {

            }
        })

        return result
    }
}