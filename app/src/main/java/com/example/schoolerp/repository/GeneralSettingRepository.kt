package com.example.schoolerp.repository


import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetAccountSettingResponse
import retrofit2.Response

class GeneralSettingRepository(private val apiService: ApiService , private val schoolId: String) {
    // This method directly calls the API
    suspend fun getAccountSetting(): Response<GetAccountSettingResponse> {
        return apiService.getAccountSetting(schoolId) // Ensure `ApiService` has this method
    }
}
