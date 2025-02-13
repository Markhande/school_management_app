package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.InstituteProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstituteRepository(private val apiService: ApiService) {

    fun getInstituteProfile(schoolId: String): LiveData<Result<InstituteProfileResponse>> {
        val result = MutableLiveData<Result<InstituteProfileResponse>>()

        apiService.getInstituteProfile(schoolId).enqueue(object : Callback<InstituteProfileResponse> {
            override fun onResponse(
                call: Call<InstituteProfileResponse>,
                response: Response<InstituteProfileResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = Result.success(response.body()!!)
                } else {
                    result.value = Result.failure(Throwable("Error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<InstituteProfileResponse>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })

        return result
    }
}

