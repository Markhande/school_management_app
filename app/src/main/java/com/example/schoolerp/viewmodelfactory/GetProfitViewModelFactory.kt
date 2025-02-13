package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetRevenueRepository
import com.example.schoolerp.repository.GetTotalProfitRepository
import com.example.schoolerp.viewmodel.GetRevenueViewModel
import com.example.schoolerp.viewmodel.GetTotalProfitViewModel

class GetProfitViewModelFactory(private val  repository: GetTotalProfitRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetTotalProfitViewModel::class.java)) {
            return GetTotalProfitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}