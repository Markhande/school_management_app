package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.AddExpenseData
import com.example.schoolerp.Api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddExpenseRepository(private val api: ApiService) {

    fun submitExpenseData(expenseData: Map<String, String>): LiveData<AddExpenseData> {
        val expenseResponse = MutableLiveData<AddExpenseData>()

        val apiService = RetrofitHelper.getApiService()
        val call = apiService.addExpense(expenseData)

        call.enqueue(object : Callback<AddExpenseData> {
            override fun onResponse(call: Call<AddExpenseData>, response: Response<AddExpenseData>) {
                if (response.isSuccessful) {
                    expenseResponse.value = response.body()
                } else {
                    expenseResponse.value = AddExpenseData(false, "API call failed")
                }
            }

            override fun onFailure(call: Call<AddExpenseData>, t: Throwable) {
                expenseResponse.value = AddExpenseData(false, "Error: ${t.message}")
            }
        })

        return expenseResponse
    }
}
