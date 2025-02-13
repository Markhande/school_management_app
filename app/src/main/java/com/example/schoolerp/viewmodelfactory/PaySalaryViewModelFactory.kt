package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.PaySalaryRepository
import com.example.schoolerp.viewmodel.PaySalaryViewModel

class PaySalaryViewModelFactory(private val repository: PaySalaryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaySalaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaySalaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
