package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.TuitionFeesRepository
import com.example.schoolerp.viewmodel.TuitionFeesViewModel

class TuitionFeesViewModelFactory(private val repository: TuitionFeesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TuitionFeesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TuitionFeesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}