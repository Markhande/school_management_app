package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.GetTimeTableRepository
import com.example.schoolerp.viewmodel.TimeTableViewModel

class GetTimeTableViewModelFactory(private val repository: GetTimeTableRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTableViewModel::class.java)) {
            return TimeTableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}