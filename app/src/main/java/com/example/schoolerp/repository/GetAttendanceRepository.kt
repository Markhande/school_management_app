package com.example.schoolerp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetStudentAttandadanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetAttendanceRepository(private val apiService: ApiService) {
    fun getStudentAttendance(schoolId: String): LiveData<GetStudentAttandadanceResponse?> {
        val data = MutableLiveData<GetStudentAttandadanceResponse?>()

        apiService.getStudentAttandadance(schoolId).enqueue(object : Callback<GetStudentAttandadanceResponse> {
            override fun onResponse(
                call: Call<GetStudentAttandadanceResponse>,
                response: Response<GetStudentAttandadanceResponse>
            ) {
                if (response.isSuccessful) {
                    // Check if response body is not null
                    if (response.body() != null) {
                        data.value = response.body()
                    } else {
                        // Handle case when response body is null
                        data.value = null
                        Log.e("API_ERROR", "Response body is null")
                    }
                } else {
                    // Handle unsuccessful response
                    data.value = null
                    Log.e("API_ERROR", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetStudentAttandadanceResponse>, t: Throwable) {
                // Handle network failure
                data.value = null
                Log.e("API_ERROR", "Failed to fetch data: ${t.message}")
            }
        })

        return data
    }
}
