package com.example.schoolerp.repository

import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.StudentAttendanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudentAttendanceRepository(private val apiService: ApiService) {

    fun addStudentAttendance(
        attendanceData: Map<String, String>,
        onResult: (StudentAttendanceResponse?) -> Unit
    ) {
        apiService.addStudentAttandance(attendanceData).enqueue(object : Callback<StudentAttendanceResponse> {
            override fun onResponse(
                call: Call<StudentAttendanceResponse>,
                response: Response<StudentAttendanceResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<StudentAttendanceResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }
}
