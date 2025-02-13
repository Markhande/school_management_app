package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddTimeTableRepository
import com.example.schoolerp.viewmodel.AddTimeTableViewModel

class AddTimeTableViewModelFactory(private val repository: AddTimeTableRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTimeTableViewModel::class.java)) {
            return AddTimeTableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
