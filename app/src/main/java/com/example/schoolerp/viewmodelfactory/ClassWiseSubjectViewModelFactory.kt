package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.ClassWiseSubjectRepository
import com.example.schoolerp.viewmodel.ClassWiseSubjectViewModel

class ClassWiseSubjectViewModelFactory(private val repository: ClassWiseSubjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassWiseSubjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClassWiseSubjectViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
