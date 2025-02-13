package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.StudentAttendanceReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentAttendanceReportRepository(private val apiService: ApiService) {

    fun getStudentAttendanceReport(schoolId: String, studentId: String): MutableLiveData<StudentAttendanceReportResponse?> {
        val result = MutableLiveData<StudentAttendanceReportResponse?>()
        apiService.getStudentAttendanceReport(schoolId, studentId).enqueue(object :
            Callback<StudentAttendanceReportResponse> {
            override fun onResponse(
                call: Call<StudentAttendanceReportResponse>,
                response: Response<StudentAttendanceReportResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(null)
                }
            }

            override fun onFailure(call: Call<StudentAttendanceReportResponse>, t: Throwable) {
                result.postValue(null)
            }
        })
        return result
    }
}

