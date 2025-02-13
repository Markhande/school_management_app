package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.EditStudentRepository
import com.example.schoolerp.repository.UpatedFeesCollectionRepository
import com.example.schoolerp.viewmodel.EditStudentViewModel
import com.example.schoolerp.viewmodel.UpateFeesCollectionViewModel

class UpdateFeesCollectionViewModelFactory (private val repository: UpatedFeesCollectionRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpateFeesCollectionViewModel::class.java)) {
            return UpateFeesCollectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
