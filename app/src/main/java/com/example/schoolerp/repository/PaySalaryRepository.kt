package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.AddaddPaySalaryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaySalaryRepository(private val apiService: ApiService) {

    fun paySalary(paySalaryData: Map<String, String>): LiveData<AddaddPaySalaryResponse> {
        val result = MutableLiveData<AddaddPaySalaryResponse>()

        apiService.addPaySalary(paySalaryData).enqueue(object : Callback<AddaddPaySalaryResponse> {
            override fun onResponse(
                call: Call<AddaddPaySalaryResponse>,
                response: Response<AddaddPaySalaryResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    result.value = AddaddPaySalaryResponse(
                        status = false,
                        message = "Error: ${response.code()} - ${response.message()}",
                        data = null
                    )
                }
            }

            override fun onFailure(call: Call<AddaddPaySalaryResponse>, t: Throwable) {
                result.value = AddaddPaySalaryResponse(
                    status = false,
                    message = t.message ?: "Unknown error occurred",
                    data = null
                )
            }
        })

        return result
    }
}
