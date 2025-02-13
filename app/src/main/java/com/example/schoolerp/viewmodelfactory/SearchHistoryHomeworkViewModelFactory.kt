package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.SearchHistoryHomeworkRepository
import com.example.schoolerp.viewmodel.SearchHistoryHomeworkViewModel

class SearchHistoryHomeworkViewModelFactory (private val homeworkRepository: SearchHistoryHomeworkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchHistoryHomeworkViewModel(homeworkRepository) as T
    }
}