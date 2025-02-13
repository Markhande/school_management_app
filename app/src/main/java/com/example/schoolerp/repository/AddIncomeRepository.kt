package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.DataClasses.AddIncomeData
import com.example.schoolerp.Api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddIncomeRepository {
    fun submitIncomeData(incomeData: Map<String, String>): LiveData<AddIncomeData> {
        val incomeResponse = MutableLiveData<AddIncomeData>()

        val apiService = RetrofitHelper.getApiService()
        val call = apiService.addIncome(incomeData)

        call.enqueue(object : Callback<AddIncomeData> {
            override fun onResponse(call: Call<AddIncomeData>, response: Response<AddIncomeData>) {
                if (response.isSuccessful) {
                    incomeResponse.value = response.body()
                } else {
                    incomeResponse.value = AddIncomeData(false, "API call failed")
                }
            }

            override fun onFailure(call: Call<AddIncomeData>, t: Throwable) {
                incomeResponse.value = AddIncomeData(false, "Error: ${t.message}")
            }
        })

        return incomeResponse
    }
}
