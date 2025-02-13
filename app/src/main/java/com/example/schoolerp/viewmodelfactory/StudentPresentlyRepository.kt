package com.example.schoolerp.viewmodelfactory

import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.StudentPresentlyModel
import retrofit2.Call

class StudentPresentlyRepository(private val apiService: ApiService) {

    fun getAttendanceSummary(schoolId: String, studentId: String): Call<StudentPresentlyModel> {
        return apiService.getAttendanceSummary(schoolId, studentId)
    }
}
