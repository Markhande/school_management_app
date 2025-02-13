package com.example.schoolerp.repository

import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetTeacherPrincipleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeAndPrincipleRepository(private val apiService: ApiService) {

    fun getTeacherPrinciple(
        schoolId: String,
        onSuccess: (GetTeacherPrincipleResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = apiService.getTeacherPrinciple(schoolId)
        call.enqueue(object : Callback<GetTeacherPrincipleResponse> {
            override fun onResponse(
                call: Call<GetTeacherPrincipleResponse>,
                response: Response<GetTeacherPrincipleResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError("Empty response")
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetTeacherPrincipleResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }
}

