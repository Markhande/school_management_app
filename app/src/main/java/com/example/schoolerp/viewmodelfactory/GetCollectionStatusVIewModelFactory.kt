package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetCollectionStatusRepository
import com.example.schoolerp.viewmodel.GetCollectionStatusViewModel
import com.example.schoolerp.viewmodel.GetTotalProfitViewModel

class GetCollectionStatusVIewModelFactory(private val repository: GetCollectionStatusRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetCollectionStatusViewModel::class.java)) {
            return GetCollectionStatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
