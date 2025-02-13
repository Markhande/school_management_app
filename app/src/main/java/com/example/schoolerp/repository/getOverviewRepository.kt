package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetMessageResponse
import com.example.schoolerp.models.responses.GetOverViewResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class getOverviewRepository(private val apiService: ApiService) {
    fun getOverview(schoolId: String): LiveData<GetOverViewResponse?> {
        val result = MutableLiveData<GetOverViewResponse?>()

        apiService.getOverView(schoolId).enqueue(object : Callback<GetOverViewResponse> {
            override fun onResponse(
                call: Call<GetOverViewResponse>,
                response: Response<GetOverViewResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(null)
                }
            }

            override fun onFailure(call: Call<GetOverViewResponse>, t: Throwable) {
                result.postValue(null)
            }
        })

        return result
    }
}