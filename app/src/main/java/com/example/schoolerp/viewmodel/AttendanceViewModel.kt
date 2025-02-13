package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.GetStudentAttandadanceResponse
import com.example.schoolerp.repository.GetAttendanceRepository

class AttendanceViewModel(private val repository: GetAttendanceRepository) : ViewModel() {
    private val _studentAttendance = MutableLiveData<GetStudentAttandadanceResponse?>()
    val studentAttendance: LiveData<GetStudentAttandadanceResponse?> get() = _studentAttendance

    // Fetch data from repository using schoolId
    fun fetchStudentAttendance(schoolId: String) {
        // Observe the LiveData returned from the repository
        repository.getStudentAttendance(schoolId).observeForever { response ->
            _studentAttendance.value = response
        }
    }
}
