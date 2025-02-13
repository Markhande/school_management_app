package com.example.schoolerp.viewmodelfactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.StudentAttendanceRepository
import com.example.schoolerp.viewmodel.StudentAttendanceViewModel

class StudentAttendanceViewModelFactory(private val repository: StudentAttendanceRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentAttendanceViewModel::class.java)) {
            return StudentAttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}