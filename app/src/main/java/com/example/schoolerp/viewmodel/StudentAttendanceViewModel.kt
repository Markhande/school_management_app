package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.StudentAttendanceResponse
import com.example.schoolerp.repository.StudentAttendanceRepository

class StudentAttendanceViewModel(private val repository: StudentAttendanceRepository) : ViewModel() {

    private val _attendanceResponse = MutableLiveData<StudentAttendanceResponse?>()
    val attendanceResponse: LiveData<StudentAttendanceResponse?> = _attendanceResponse

    fun addStudentAttendance(attendanceData: Map<String, String>) {
        repository.addStudentAttendance(attendanceData) { response ->
            _attendanceResponse.postValue(response)
        }
    }

}
