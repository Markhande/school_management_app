package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetRevenueRepository
import com.example.schoolerp.viewmodel.GetRevenueViewModel

class GetRevenueViewModelFactory (private val repository: GetRevenueRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetRevenueViewModel::class.java)) {
            return GetRevenueViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}