package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.AddTimeTableResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTimeTableRepository(private var apiService: ApiService) {
    fun addTimeTable(data: Map<String, String>): LiveData<Result<AddTimeTableResponse>> {
        val result = MutableLiveData<Result<AddTimeTableResponse>>()

        apiService.addTimeTable(data).enqueue(object : Callback<AddTimeTableResponse> {
            override fun onResponse(
                call: Call<AddTimeTableResponse>,
                response: Response<AddTimeTableResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()) as Result<AddTimeTableResponse>?)
                } else {
                    result.postValue(Result.failure(Exception("Failed to add timetable: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<AddTimeTableResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}
