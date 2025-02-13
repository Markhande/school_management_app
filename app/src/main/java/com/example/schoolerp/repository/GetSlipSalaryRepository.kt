package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetSalarySlipResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetSlipSalaryRepository(private val apiService: ApiService) {
    fun getSalarySlip(schoolId: String): LiveData<GetSalarySlipResponse> {
        val result= MutableLiveData<GetSalarySlipResponse>()

        apiService.getSalarySlip(schoolId).enqueue(object : Callback<GetSalarySlipResponse> {
            override fun onResponse(
                call: Call<GetSalarySlipResponse>,
                response: Response<GetSalarySlipResponse>
            ) {
                if (response.isSuccessful){
                    result.value=response.body()

                }else{
                    result.value = GetSalarySlipResponse(status = false, message = "Error fetching data", data = null)


                }
            }

            override fun onFailure(call: Call<GetSalarySlipResponse>, t: Throwable) {
                result.value = GetSalarySlipResponse(status = false, message = t.message ?: "Unknown error", data = null)
            }
        })

        return result
    }
}



