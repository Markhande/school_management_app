package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.PutMessageRepository
import com.example.schoolerp.viewmodel.PutMessageViewModel
import com.example.schoolerp.viewmodel.PutTimeTableViewModel

class PutMessageViewModelFactory(private val repository: PutMessageRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PutMessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PutMessageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

