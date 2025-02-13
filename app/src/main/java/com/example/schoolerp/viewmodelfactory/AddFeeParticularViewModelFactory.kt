package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddFeeParticularRepository
import com.example.schoolerp.viewmodel.AddFeeParticularViewModel

class AddFeeParticularViewModelFactory(private val repository: AddFeeParticularRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFeeParticularViewModel::class.java)) {
            return AddFeeParticularViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}