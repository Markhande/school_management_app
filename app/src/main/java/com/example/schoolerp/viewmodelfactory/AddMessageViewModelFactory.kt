package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddMessageRepository
import com.example.schoolerp.viewmodel.AddMessageViewModel

class AddMessageViewModelFactory(private val repository: AddMessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMessageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}