package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.AddaccountchartResponse
import com.example.schoolerp.models.responses.PutTimeTableResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddaccountchartRepository(private val apiService: ApiService){
    fun AddchartAcount(paySalaryData: Map<String, String>): LiveData<AddaccountchartResponse> {
        val result = MutableLiveData<AddaccountchartResponse>()

        apiService.addaccountchart(paySalaryData).enqueue(object : Callback<AddaccountchartResponse> {
            override fun onResponse(
                call: Call<AddaccountchartResponse>,
                response: Response<AddaccountchartResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    result.value = AddaccountchartResponse(
                        status = false,
                        message = "Error: ${response.code()} - ${response.message()}",

                        )
                }
            }

            override fun onFailure(call: Call<AddaccountchartResponse>, t: Throwable) {
                result.value = AddaccountchartResponse(
                    status = false,
                    message = t.message ?: "Unknown error occurred",
                )
            }
        })

        return result
    }
}

