package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.InstituteRepository
import com.example.schoolerp.viewmodel.SchoolProfileViewModel

class SchoolProfileViewModelFactory(
    private val repository: InstituteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchoolProfileViewModel::class.java)) {
            return SchoolProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
