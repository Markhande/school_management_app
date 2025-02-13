package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.schoolerp.repository.StudentAttendanceReportRepository
import com.example.schoolerp.viewmodel.StudentAttendanceReportViewModel

class StudentAttendanceReportViewModelFactory(private val repository: StudentAttendanceReportRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return StudentAttendanceReportViewModel(repository) as T
    }
}
