package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.getSubjectRepository
import com.example.schoolerp.viewmodel.SubjectViewModel
import com.example.schoolerp.viewmodel.getSubjectViewModel

class getSubjectViewModelFactory(
    private val repository: getSubjectRepository,
    private val schoolId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(getSubjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return getSubjectViewModel(repository, schoolId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
