package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.BaseOnInputeMessageRepository
import com.example.schoolerp.viewmodel.BaseOnInputMessageViewModel

class BaseOnInputeMessageViewModelFactory (private val repository: BaseOnInputeMessageRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseOnInputMessageViewModel::class.java)) {
            return BaseOnInputMessageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}