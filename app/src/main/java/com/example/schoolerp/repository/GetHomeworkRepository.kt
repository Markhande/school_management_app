package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.DeleteHomeworkResponse
import com.example.schoolerp.models.responses.GetHomeworkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetHomeworkRepository(private val apiService: ApiService)  {

    fun fetchHomeWork(schoolId: String, employeeId: String): LiveData<Result<GetHomeworkResponse>> {
        val data = MutableLiveData<Result<GetHomeworkResponse>>()

        apiService.getHomeWork(schoolId, employeeId).enqueue(object : Callback<GetHomeworkResponse> {
            override fun onResponse(call: Call<GetHomeworkResponse>, response: Response<GetHomeworkResponse>) {
                if (response.isSuccessful) {
                    data.postValue(Result.success(response.body()!!))
                } else {
                    data.postValue(Result.failure(Exception(response.message())))
                }
            }

            override fun onFailure(call: Call<GetHomeworkResponse>, t: Throwable) {
                data.postValue(Result.failure(t))
            }
        })

        return data
    }
    fun deleteHomework(schoolId: String, homeworkId: String): LiveData<Result<DeleteHomeworkResponse>> {
        val result = MutableLiveData<Result<DeleteHomeworkResponse>>()

        apiService.deleteHomeWork(schoolId, homeworkId).enqueue(object : Callback<DeleteHomeworkResponse> {
            override fun onResponse(
                call: Call<DeleteHomeworkResponse>,
                response: Response<DeleteHomeworkResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    result.value = Result.success(response.body()!!)
                } else {
                    result.value = Result.failure(Exception("Error: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<DeleteHomeworkResponse>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }
}
