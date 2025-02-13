package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubjectRepository(private val api: ApiService) {
     fun addSubject(fields: Map<String, String?>): LiveData<ApiResponse> {
        val result = MutableLiveData<ApiResponse>()

        api.addSubject(fields).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    result.value = response.body()
                } else {
                    result.value = ApiResponse(message = "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                result.value = ApiResponse(message = t.localizedMessage)
            }
        })

        return result
    }
}
