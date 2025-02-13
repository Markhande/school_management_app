package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.EmloyeesAttendanceRepository
import com.example.schoolerp.viewmodel.EmployeesAttendanceViewModel


class EmployeesAttendanceViewModelFactory(
    private val repository: EmloyeesAttendanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeesAttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeesAttendanceViewModel( repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}