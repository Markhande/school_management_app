package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetMessageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetMessageRepository(private val apiService: ApiService) {

    fun getMessages(schoolId: String, employee: String): LiveData<GetMessageResponse?> {
        val result = MutableLiveData<GetMessageResponse?>()

        apiService.getMessage(schoolId, employee).enqueue(object : Callback<GetMessageResponse> {
            override fun onResponse(
                call: Call<GetMessageResponse>,
                response: Response<GetMessageResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(null)
                }
            }

            override fun onFailure(call: Call<GetMessageResponse>, t: Throwable) {
                result.postValue(null)
            }
        })

        return result
    }
    fun deleteMessage(schoolId: String, Id: String): LiveData<Result<DeleteResponse>> {
        val result = MutableLiveData<Result<DeleteResponse>>()

        apiService.deleteMessage(schoolId, Id).enqueue(object : Callback<DeleteResponse> {
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





