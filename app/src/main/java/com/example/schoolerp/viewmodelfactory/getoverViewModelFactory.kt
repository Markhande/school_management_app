package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.getOverviewRepository
import com.example.schoolerp.viewmodel.GetTotalProfitViewModel
import com.example.schoolerp.viewmodel.getOverViewModel

class getoverViewModelFactory(private val repository: getOverviewRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(getOverViewModel::class.java)) {
            return getOverViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
