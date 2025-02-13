package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetRevenueRepository(private val apiService: ApiService) {

    fun getRevenue(schoolId: String): LiveData<GetgetRevenueResponse> {
        val result = MutableLiveData<GetgetRevenueResponse>()

        // Make the API request
        apiService.getRevenue(schoolId).enqueue(object : Callback<GetgetRevenueResponse> {
            override fun onResponse(
                call: Call<GetgetRevenueResponse>,
                response: Response<GetgetRevenueResponse>
            ) {
                if (response.isSuccessful) {
                    // Successfully received the data, update the LiveData
                    result.value = response.body()
                } else {
                    // Handle failure scenario (if necessary)
                   result.value = GetgetRevenueResponse(status = false, message = "Error fetching data", data = null)
                }
            }

            override fun onFailure(call: Call<GetgetRevenueResponse>, t: Throwable) {
                // Handle network failure or other errors
                result.value = GetgetRevenueResponse(status = false, message = t.message ?: "Unknown error", data = null)
            }
        })

        return result
    }
}
