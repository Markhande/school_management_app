package com.example.schoolerp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetStudentAttandadanceDateWiseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DateWiseStudentAttendanceRepository(private val apiService: ApiService) {

    private val _attendanceData = MutableLiveData<GetStudentAttandadanceDateWiseResponse>()
    val attendanceData: LiveData<GetStudentAttandadanceDateWiseResponse> get() = _attendanceData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAttendanceDateWise(schoolId: String,class_name:String) {
        apiService.getStudentAttandadancedateWise(schoolId,class_name).enqueue(object :
            Callback<GetStudentAttandadanceDateWiseResponse> {
            override fun onResponse(
                call: Call<GetStudentAttandadanceDateWiseResponse>,
                response: Response<GetStudentAttandadanceDateWiseResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _attendanceData.postValue(response.body())
                } else {
                    _error.postValue("Failed to load attendance")
                }
            }

            override fun onFailure(call: Call<GetStudentAttandadanceDateWiseResponse>, t: Throwable) {
                _error.postValue(t.message ?: "Unknown error occurred")
            }
        })
    }
}
