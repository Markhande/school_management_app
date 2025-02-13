package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.EmpAttendanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeAttendanceRepository(private val apiService: ApiService) {

    // LiveData for storing the attendance response
    private val _attendanceResponse = MutableLiveData<EmpAttendanceResponse>()
    val attendanceResponse: LiveData<EmpAttendanceResponse> get() = _attendanceResponse

    // LiveData for storing errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Function to add employee attendance
    fun addAttendance(attendanceData: Map<String, String>) {
        val call = apiService.addEmpAttandance(attendanceData)

        call.enqueue(object : Callback<EmpAttendanceResponse> {
            override fun onResponse(call: Call<EmpAttendanceResponse>, response: Response<EmpAttendanceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _attendanceResponse.value = response.body()
                } else {
                    _error.value = "Failed to add attendance"
                }
            }

            override fun onFailure(call: Call<EmpAttendanceResponse>, t: Throwable) {
                _error.value = t.message ?: "An error occurred"
            }
        })
    }
}
