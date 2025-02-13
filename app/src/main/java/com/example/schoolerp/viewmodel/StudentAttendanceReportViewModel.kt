package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.StudentAttendanceReportResponse
import com.example.schoolerp.repository.StudentAttendanceReportRepository

class StudentAttendanceReportViewModel(private val repository: StudentAttendanceReportRepository) : ViewModel() {
    private val _attendanceReport = MutableLiveData<StudentAttendanceReportResponse>()
    val attendanceReport: LiveData<StudentAttendanceReportResponse> get() = _attendanceReport

    fun fetchStudentAttendanceReport(schoolId: String, extraParam: String) {
        // Your logic for fetching the data from the repository
        repository.getStudentAttendanceReport(schoolId, extraParam).observeForever { response ->
            _attendanceReport.postValue(response!!)
        }
    }
}


