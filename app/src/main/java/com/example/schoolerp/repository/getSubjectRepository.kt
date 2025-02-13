package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.DataClasses.ClassWithSubjects
import com.example.schoolerp.models.responses.SubjectDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class getSubjectRepository(private val apiService: ApiService) {
    fun getSubjects(schoolid: String): LiveData<List<ClassWithSubjects>> {
        val data = MutableLiveData<List<ClassWithSubjects>>()
        apiService.getSubjeact(schoolid).enqueue(object : Callback<SubjectDataResponse> {
            override fun onResponse(
                call: Call<SubjectDataResponse>,
                response: Response<SubjectDataResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    data.value = response.body()?.data ?: emptyList()
                } else {
                    data.value = emptyList()
                }
            }

            override fun onFailure(call: Call<SubjectDataResponse>, t: Throwable) {
                data.value = emptyList()
            }
        })
        return data
    }
}
