package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddCalenderRepository
import com.example.schoolerp.repository.GetCalenderRepository
import com.example.schoolerp.viewmodel.GetCalenderViewModel
import com.example.schoolerp.viewmodel.GetCollectionStatusViewModel

class GetCalenderViewModelFactory (private val repository: GetCalenderRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetCalenderViewModel::class.java)) {
            return GetCalenderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
