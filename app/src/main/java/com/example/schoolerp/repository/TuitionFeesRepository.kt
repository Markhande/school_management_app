package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.ClassAndStudentGetTuitionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TuitionFeesRepository(private val apiService: ApiService) {

    fun getTuitionData(params: Map<String, String>): LiveData<ClassAndStudentGetTuitionResponse> {
        val result = MutableLiveData<ClassAndStudentGetTuitionResponse>()

        apiService.inputClassAndStudentGetTuitionData(params).enqueue(object :
            Callback<ClassAndStudentGetTuitionResponse> {
            override fun onResponse(
                call: Call<ClassAndStudentGetTuitionResponse>,
                response: Response<ClassAndStudentGetTuitionResponse>
            ) {
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(null)
                }
            }

            override fun onFailure(call: Call<ClassAndStudentGetTuitionResponse>, t: Throwable) {
                result.postValue(null)
            }
        })

        return result
    }
}
