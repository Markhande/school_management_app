package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.EmployeeAttendanceRepository
import com.example.schoolerp.repository.StudentAttendanceRepository
import com.example.schoolerp.viewmodel.EmployeeAttendanceViewModel
import com.example.schoolerp.viewmodel.StudentAttendanceViewModel

class EmployeeAttendanceViewModelFactory(
    private val repository: EmployeeAttendanceRepository
) : ViewModelProvider.Factory {

    // Override the create method to return the ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the modelClass is EmployeeAttendanceViewModel and return the instance
        if (modelClass.isAssignableFrom(EmployeeAttendanceViewModel::class.java)) {
            return EmployeeAttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}