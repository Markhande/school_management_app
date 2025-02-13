package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AllEmployeesRepository
import com.example.schoolerp.viewmodel.AllEmployeesViewModel

class AllEmployeesViewModelFactory(private val repository: AllEmployeesRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllEmployeesViewModel::class.java)) {
            return AllEmployeesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
