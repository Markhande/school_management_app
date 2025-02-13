package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetTimeTableResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetTimeTableRepository(private val apiService: ApiService) {

    fun fetchTimeTable(schoolId: String): LiveData<Result<GetTimeTableResponse>> {
        val result = MutableLiveData<Result<GetTimeTableResponse>>()

        apiService.getTimeTable(schoolId).enqueue(object : Callback<GetTimeTableResponse> {
            override fun onResponse(
                call: Call<GetTimeTableResponse>,
                response: Response<GetTimeTableResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()) as Result<GetTimeTableResponse>?)
                } else {
                    result.postValue(
                        Result.failure(
                            Exception(
                                "Failed to fetch timetable: ${
                                    response.errorBody()?.string()
                                }"
                            )
                        )
                    )
                }
            }

            override fun onFailure(call: Call<GetTimeTableResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
    fun deleteTimeTable(schoolId: String, employeeId: String): LiveData<Result<DeleteResponse>> {
        val result = MutableLiveData<Result<DeleteResponse>>()

        apiService.deleteTimeTable(schoolId, employeeId).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()) as Result<DeleteResponse>?)
                } else {
                    result.postValue(Result.failure(Exception("Failed to delete timetable")))
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}


