package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddHomeworkRepository
import com.example.schoolerp.viewmodel.AddHomeworkViewModel

class AddHomeworkViewModelFactoryclass(private val repository: AddHomeworkRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddHomeworkViewModel::class.java)) {
            return AddHomeworkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
