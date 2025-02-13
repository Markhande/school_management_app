package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetAcountChartResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class getAcountChartRepository(private val apiService: ApiService) {
    fun GetAcountChart(schoolId: String): LiveData<GetAcountChartResponse> {
        val result = MutableLiveData<GetAcountChartResponse>()

        // Make the API request
        apiService.getAcountChart(schoolId).enqueue(object : Callback<GetAcountChartResponse> {
            override fun onResponse(
                call: Call<GetAcountChartResponse>,
                response: Response<GetAcountChartResponse>
            ) {
                if (response.isSuccessful) {
                    // Successfully received the data, update the LiveData
                    result.value = response.body()
                } else {
                    // Handle failure scenario (if necessary)
                    result.value = GetAcountChartResponse(status = false, message = "Error fetching data", data = null)
                }
            }

            override fun onFailure(call: Call<GetAcountChartResponse>, t: Throwable) {
                // Handle network failure or other errors
                result.value = GetAcountChartResponse(status = false, message = t.message ?: "Unknown error", data = null)
            }
        })

        return result
    }
    fun deleteAcountChart(schoolId: String, Id: String): LiveData<Result<DeleteResponse>> {
        val result = MutableLiveData<Result<DeleteResponse>>()

        apiService.deleteChartAcount(schoolId, Id).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()) as Result<DeleteResponse>?)
                } else {
                    result.postValue(Result.failure(Exception("Failed to delete timetable")))
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}



