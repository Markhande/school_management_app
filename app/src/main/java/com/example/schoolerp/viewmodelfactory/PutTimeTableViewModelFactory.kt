package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.PaySalaryRepository
import com.example.schoolerp.repository.PutTimeTableRepository
import com.example.schoolerp.viewmodel.PaySalaryViewModel
import com.example.schoolerp.viewmodel.PutTimeTableViewModel

class PutTimeTableViewModelFactory(private val repository: PutTimeTableRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PutTimeTableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PutTimeTableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
