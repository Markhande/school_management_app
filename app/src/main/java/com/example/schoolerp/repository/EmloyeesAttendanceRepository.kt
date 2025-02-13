package com.example.schoolerp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.EmployeeAttendance
import com.example.schoolerp.models.responses.GetEmployeesAttandadanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmloyeesAttendanceRepository(private val apiService: ApiService) {

    fun getEmployeesAttendance(schoolId: String): LiveData<GetEmployeesAttandadanceResponse?> {
        val data = MutableLiveData<GetEmployeesAttandadanceResponse?>()

        apiService.getEmployeesAttandadance(schoolId).enqueue(object : Callback<GetEmployeesAttandadanceResponse> {
            override fun onResponse(
                call: Call<GetEmployeesAttandadanceResponse>,
                response: Response<GetEmployeesAttandadanceResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        data.value = it
                    } ?: run {
                        data.value = null
                        Log.e("API_ERROR", "Employee response body is null")
                    }
                } else {
                    data.value = null
                    Log.e("API_ERROR", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetEmployeesAttandadanceResponse>, t: Throwable) {
                data.value = null
                Log.e("API_ERROR", "Failed to fetch employee data: ${t.message}")
            }
        })

        return data
    }
}