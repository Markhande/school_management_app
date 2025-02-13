package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetFeeParticularRepository
import com.example.schoolerp.viewmodel.GetFeeParticularViewModel

class GetFeeParticularViewModelFactory(private val repository: GetFeeParticularRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetFeeParticularViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetFeeParticularViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}