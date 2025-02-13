package com.example.schoolerp.repository

import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.models.responses.getStudent_Idcard_Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentRepository {

    private val apiService = RetrofitHelper.getApiService()

    fun getStudentIdCardDetails(schoolId: String, callback: (Result<getStudent_Idcard_Response>) -> Unit) {
        val call = apiService.getStudentIdcardDetails(schoolId)
        call.enqueue(object : Callback<getStudent_Idcard_Response> {
            override fun onResponse(
                call: Call<getStudent_Idcard_Response>,
                response: Response<getStudent_Idcard_Response>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable("Failed to fetch data")))
                }
            }

            override fun onFailure(call: Call<getStudent_Idcard_Response>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}