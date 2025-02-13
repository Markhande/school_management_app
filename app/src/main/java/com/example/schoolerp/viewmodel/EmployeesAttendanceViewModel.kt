package com.example.schoolerp.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.Api.RetrofitHelper
import com.example.schoolerp.models.responses.GetEmployeesAttandadanceResponse
import com.example.schoolerp.models.responses.GetStudentAttandadanceResponse
import com.example.schoolerp.repository.EmloyeesAttendanceRepository
import com.example.schoolerp.repository.GetAttendanceRepository


class EmployeesAttendanceViewModel(private val repository: EmloyeesAttendanceRepository) :ViewModel(){

    private val _employeeAttendanceData = MutableLiveData<GetEmployeesAttandadanceResponse?>()
    val employeeAttendanceData: LiveData<GetEmployeesAttandadanceResponse?> get() = _employeeAttendanceData

    fun getEmployeeAttendance(schoolId: String) {
        repository.getEmployeesAttendance(schoolId).observeForever { response ->
            _employeeAttendanceData.value = response
        }
    }
}