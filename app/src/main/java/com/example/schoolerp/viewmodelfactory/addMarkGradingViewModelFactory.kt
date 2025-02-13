package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddMarkGradingRepository
import com.example.schoolerp.viewmodel.AddMarkGradingViewModel
import com.example.schoolerp.viewmodel.AddMessageViewModel

class addMarkGradingViewModelFactory(private val repository: AddMarkGradingRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMarkGradingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMarkGradingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
