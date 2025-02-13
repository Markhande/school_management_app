package com.example.schoolerp.repository

import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.LoginResponse
import retrofit2.Call

class UserRepository(private val api: ApiService) {
    fun login(
        schoolId: String,
        password: String
    ):
        Call<LoginResponse> {
        return api.login(schoolId, password)
    }
}
