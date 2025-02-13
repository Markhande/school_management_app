package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.AddaddPaySalaryResponse
import com.example.schoolerp.models.responses.PutTimeTableResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PutTimeTableRepository(private val apiService: ApiService) {

    fun PutTimeTable(paySalaryData: Map<String, String>): LiveData<PutTimeTableResponse> {
        val result = MutableLiveData<PutTimeTableResponse>()

        apiService.PutTimeTable(paySalaryData).enqueue(object : Callback<PutTimeTableResponse> {
            override fun onResponse(
                call: Call<PutTimeTableResponse>,
                response: Response<PutTimeTableResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    result.value = PutTimeTableResponse(
                        status = false.toString(),
                        message = "Error: ${response.code()} - ${response.message()}",

                    )
                }
            }

            override fun onFailure(call: Call<PutTimeTableResponse>, t: Throwable) {
                result.value = PutTimeTableResponse(
                    status = false.toString(),
                    message = t.message ?: "Unknown error occurred",
                )
            }
        })

        return result
    }
}

