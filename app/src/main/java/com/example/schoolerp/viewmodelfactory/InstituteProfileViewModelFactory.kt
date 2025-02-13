package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.InstituteProfileRepository
import com.example.schoolerp.viewmodel.InstituteProfileViewModel

class InstituteProfileViewModelFactory(private val repository: InstituteProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstituteProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstituteProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
