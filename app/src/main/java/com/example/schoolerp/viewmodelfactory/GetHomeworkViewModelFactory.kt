package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetHomeworkRepository
import com.example.schoolerp.viewmodel.GetHomeworkViewModel

class GetHomeworkViewModelFactory(private val repository: GetHomeworkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetHomeworkViewModel::class.java)) {
            return GetHomeworkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}