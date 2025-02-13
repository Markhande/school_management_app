package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetSlipSalaryRepository
import com.example.schoolerp.viewmodel.GetSalarySlipViewModel

class GetSalarySlipViewModelFactory(private val repository: GetSlipSalaryRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetSalarySlipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetSalarySlipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

