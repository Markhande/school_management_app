package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.EmpAttendanceResponse
import com.example.schoolerp.repository.EmployeeAttendanceRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeAttendanceViewModel(private val repository: EmployeeAttendanceRepository) : ViewModel() {

    // LiveData for the attendance response
    private val _attendanceResponse = MutableLiveData<EmpAttendanceResponse>()
    val attendanceResponse: LiveData<EmpAttendanceResponse> get() = _attendanceResponse

    // LiveData for errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Method to add employee attendance, calls the repository
    fun addEmployeeAttendance(attendanceData: Map<String, String>) {
        repository.addAttendance(attendanceData)
    }
}
