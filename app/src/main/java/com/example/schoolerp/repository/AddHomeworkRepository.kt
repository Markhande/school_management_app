package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.HomeWorkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddHomeworkRepository(private val apiService: ApiService) {
    fun addHomework(homeWorkData: Map<String, String>): LiveData<HomeWorkResponse> {
        val result = MutableLiveData<HomeWorkResponse>()

        apiService.addHomework(homeWorkData).enqueue(object : Callback<HomeWorkResponse> {
            override fun onResponse(call: Call<HomeWorkResponse>, response: Response<HomeWorkResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    result.value = response.body() // Assign server response to result
                } else {
                    // Create a custom response indicating the error

                }
            }

            override fun onFailure(call: Call<HomeWorkResponse>, t: Throwable) {
                // Create a custom response indicating the failure

            }
        })

        return result
    }
    fun updatedHomework(homeWorkData: Map<String, String>): LiveData<HomeWorkResponse> {
        val result = MutableLiveData<HomeWorkResponse>()

        apiService.UpdatedHomework(homeWorkData).enqueue(object : Callback<HomeWorkResponse> {
            override fun onResponse(call: Call<HomeWorkResponse>, response: Response<HomeWorkResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    result.value = response.body() // Assign server response to result
                } else {
                    // Create a custom response indicating the error

                }
            }

            override fun onFailure(call: Call<HomeWorkResponse>, t: Throwable) {
                // Create a custom response indicating the failure

            }
        })

        return result
    }

}
