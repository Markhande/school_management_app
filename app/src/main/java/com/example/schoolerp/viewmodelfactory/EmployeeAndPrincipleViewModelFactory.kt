package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.DataClasses.EmployeeAndPrinciple
import com.example.schoolerp.repository.EmployeeAndPrincipleRepository
import com.example.schoolerp.viewmodel.EmployeeAndPrincipleViewModel

class EmployeeAndPrincipleViewModelFactory (private val repository: EmployeeAndPrincipleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeAndPrincipleViewModel::class.java)) {
            return EmployeeAndPrincipleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}