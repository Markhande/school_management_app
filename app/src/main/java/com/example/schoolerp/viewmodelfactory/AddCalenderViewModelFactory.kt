package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddCalenderRepository
import com.example.schoolerp.repository.AddExpenseRepository
import com.example.schoolerp.viewmodel.AddCalenderViewModel
import com.example.schoolerp.viewmodel.AddExpenseViewModel

class AddCalenderViewModelFactory (private val repository:AddCalenderRepository):ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddCalenderViewModel::class.java)) {
                return AddCalenderViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
