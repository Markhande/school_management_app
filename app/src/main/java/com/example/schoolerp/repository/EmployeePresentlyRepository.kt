package com.example.schoolerp.repository

import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.models.responses.StudentPresentlyModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeePresentlyRepository {

    private val retrofitService = RetrofitHelper.getApiService()

    fun getEmployeeSummary(schoolId: String, studentId: String, callback: (StudentPresentlyModel?) -> Unit) {
        retrofitService.getAttendanceEmpSummary(schoolId, studentId).enqueue(object : Callback<StudentPresentlyModel> {
            override fun onResponse(call: Call<StudentPresentlyModel>, response: Response<StudentPresentlyModel>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<StudentPresentlyModel>, t: Throwable) {
                callback(null)
            }
        })
    }
}
