package com.example.schoolerp.repository


import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.LogoutResponse
import retrofit2.Call

class LogoutRepository(private val apiService: ApiService) {

    // The repository makes the API call and returns the response as a Call
    fun logout(): Call<LogoutResponse> {
        return apiService.logout()
    }
}

