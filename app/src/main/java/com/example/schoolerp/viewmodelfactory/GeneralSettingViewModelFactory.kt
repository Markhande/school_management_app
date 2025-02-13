package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GeneralSettingRepository
import com.example.schoolerp.viewmodel.GeneralSettingViewModel

class GeneralSettingViewModelFactory(private val repository: GeneralSettingRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeneralSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeneralSettingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}