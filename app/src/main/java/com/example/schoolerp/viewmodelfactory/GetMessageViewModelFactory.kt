package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetMessageRepository
import com.example.schoolerp.viewmodel.GetMessageViewModel

class GetMessageViewModelFactory (private val repository: GetMessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetMessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetMessageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
