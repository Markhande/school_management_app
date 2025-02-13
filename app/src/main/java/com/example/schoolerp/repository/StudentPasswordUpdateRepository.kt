package com.example.schoolerp.repository

import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.updateStudentPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentPasswordUpdateRepository {

    fun updateStudentPassword(
        studentDetails: Map<String, String>,
        callback: Callback<updateStudentPasswordResponse>
    ) {
        val apiService = RetrofitHelper.getApiService()
        val call = apiService.UpdateStudentPassword(studentDetails)
        call.enqueue(callback)
    }
}