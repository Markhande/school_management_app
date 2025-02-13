package com.example.schoolerp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.DataClasses.updateStudentPasswordResponse
import com.example.schoolerp.repository.StudentPasswordUpdateRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentPasswordUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentPasswordUpdateRepository()
    val updatePasswordResponse: MutableLiveData<updateStudentPasswordResponse> = MutableLiveData()

    fun updatePassword(studentDetails: Map<String, String>) {
        viewModelScope.launch {
            repository.updateStudentPassword(studentDetails, object : Callback<updateStudentPasswordResponse> {
                override fun onResponse(
                    call: Call<updateStudentPasswordResponse>,
                    response: Response<updateStudentPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        updatePasswordResponse.value = response.body()
                    } else {
                        // Handle unsuccessful response
                    }
                }

                override fun onFailure(call: Call<updateStudentPasswordResponse>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }
}
