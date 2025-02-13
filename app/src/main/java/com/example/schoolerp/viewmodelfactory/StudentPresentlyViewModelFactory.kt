package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.viewmodel.StudentPresentlyViewModel

class StudentPresentlyViewModelFactory(private val repository: StudentPresentlyRepository) : ViewModelProvider.Factory {

    // Corrected: The method 'create' should be an override.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentPresentlyViewModel::class.java)) {
            return StudentPresentlyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
