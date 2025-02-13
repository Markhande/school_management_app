package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddExpenseRepository
import com.example.schoolerp.repository.EmployeeRepository
import com.example.schoolerp.viewmodel.AddExpenseViewModel
import com.example.schoolerp.viewmodel.AddIncomeViewModel
import com.example.schoolerp.viewmodel.AddNewEmployeesViewModel

class AddExpenseViewModelFactory(private val repository: AddExpenseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            return AddExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}