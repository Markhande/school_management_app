package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ApiResult
import com.example.schoolerp.models.responses.LogoutResponse
import com.example.schoolerp.repository.LogoutRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutViewModel(private val repository: LogoutRepository) : ViewModel() {

    // LiveData to hold the status of the logout process
    private val _logoutStatus = MutableLiveData<ApiResult<Boolean>>()
    val logoutStatus: LiveData<ApiResult<Boolean>> get() = _logoutStatus

    // Function to perform the logout process
    fun logout() {
        _logoutStatus.value = ApiResult.Loading // Indicate loading state

        // Make the API call through the repository
        repository.logout().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val logoutResponse = response.body()!!

                    if (logoutResponse.status) {
                        _logoutStatus.value = ApiResult.Success(true) // Success state
                    } else {
                        _logoutStatus.value = ApiResult.Error(logoutResponse.message) // Error state
                    }
                } else {
                    _logoutStatus.value = ApiResult.Error("Failed to logout: Unknown error") // Error state
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                _logoutStatus.value = ApiResult.Error("Logout failed: ${t.message}") // Error state
            }
        })
    }
}
