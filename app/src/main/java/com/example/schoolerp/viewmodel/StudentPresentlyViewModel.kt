package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.StudentPresentlyModel
import com.example.schoolerp.viewmodelfactory.StudentPresentlyRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentPresentlyViewModel(private val repository: StudentPresentlyRepository) : ViewModel() {

    private val _attendanceSummary = MutableLiveData<StudentPresentlyModel>()
    val attendanceSummary: LiveData<StudentPresentlyModel> = _attendanceSummary

    fun fetchAttendanceSummary(schoolId: String, studentId: String) {
        repository.getAttendanceSummary(schoolId, studentId).enqueue(object :
            Callback<StudentPresentlyModel> {
            override fun onResponse(
                call: Call<StudentPresentlyModel>,
                response: Response<StudentPresentlyModel>
            ) {
                if (response.isSuccessful) {
                    _attendanceSummary.value = response.body()
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<StudentPresentlyModel>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
