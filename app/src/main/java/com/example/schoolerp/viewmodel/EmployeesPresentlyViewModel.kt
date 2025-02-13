package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.StudentPresentlyModel
import com.example.schoolerp.repository.EmployeePresentlyRepository

class EmployeesPresentlyViewModel : ViewModel() {

    private val repository = EmployeePresentlyRepository()

    private val _employeeSummary = MutableLiveData<StudentPresentlyModel?>()
    val employeeSummary: MutableLiveData<StudentPresentlyModel?> get() = _employeeSummary

    fun fetchEmployeeSummary(schoolId: String, studentId: String) {
        repository.getEmployeeSummary(schoolId, studentId) { data ->
            _employeeSummary.postValue(data)
        }
    }
}
