package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.PutHomeWorkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PutMessageRepository(private val apiService: ApiService) {
    fun PutHomeWork(paySalaryData: Map<String, String>): LiveData<PutHomeWorkResponse> {
        val result = MutableLiveData<PutHomeWorkResponse>()

        apiService.PutHomeWork(paySalaryData).enqueue(object : Callback<PutHomeWorkResponse> {
            override fun onResponse(
                call: Call<PutHomeWorkResponse>,
                response: Response<PutHomeWorkResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    result.value = PutHomeWorkResponse(
                        status = false.toString(),
                        message = "Error: ${response.code()} - ${response.message()}",

                        )
                }
            }

            override fun onFailure(call: Call<PutHomeWorkResponse>, t: Throwable) {
                result.value = PutHomeWorkResponse(
                    status = false.toString(),
                    message = t.message ?: "Unknown error occurred",
                )
            }
        })

        return result
    }
}