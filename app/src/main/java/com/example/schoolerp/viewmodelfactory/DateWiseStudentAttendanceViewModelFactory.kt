package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.DateWiseStudentAttendanceRepository
import com.example.schoolerp.viewmodel.DateWiseStudentAttendanceViewModel

class DateWiseStudentAttendanceViewModelFactory(
    private val repository: DateWiseStudentAttendanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DateWiseStudentAttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DateWiseStudentAttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
