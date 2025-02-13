package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.ClassWiseSubjectResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassWiseSubjectRepository(private val apiService: ApiService) {

    fun fetchSubjectsByClass(classWiseSubjectData: Map<String, String>): LiveData<ClassWiseSubjectResponse> {
        val data = MutableLiveData<ClassWiseSubjectResponse>()
        apiService.addClassWiseSubject(classWiseSubjectData).enqueue(object :
            Callback<ClassWiseSubjectResponse> {
            override fun onResponse(
                call: Call<ClassWiseSubjectResponse>,
                response: Response<ClassWiseSubjectResponse>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                   // data.value = null // Handle the error appropriately
                }
            }

            override fun onFailure(call: Call<ClassWiseSubjectResponse>, t: Throwable) {
                //data.value = null // Handle the failure appropriately
            }
        })
        return data
    }
}
