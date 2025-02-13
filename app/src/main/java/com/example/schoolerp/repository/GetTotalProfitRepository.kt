package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetTotalProfitResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetTotalProfitRepository(val apiService: ApiService) {
    fun getTotalprofit(schoolId: String):LiveData<GetTotalProfitResponse>{
        val result=MutableLiveData<GetTotalProfitResponse>()

        apiService.getTotalProfit(schoolId).enqueue(object :Callback<GetTotalProfitResponse>{
            override fun onResponse(
                call: Call<GetTotalProfitResponse>,
                response: Response<GetTotalProfitResponse>
            ) {
                if (response.isSuccessful){
                    result.value=response.body()

                }else{
                    result.value = GetTotalProfitResponse(status = false, message = "Error fetching data", data = null)


                }
            }

            override fun onFailure(call: Call<GetTotalProfitResponse>, t: Throwable) {
                result.value = GetTotalProfitResponse(status = false, message = t.message ?: "Unknown error", data = null)
            }
        })

        return result
    }
}


