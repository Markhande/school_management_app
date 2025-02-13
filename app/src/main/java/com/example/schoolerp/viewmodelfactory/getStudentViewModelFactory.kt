package com.example.schoolerp.viewmodelfactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.getStudentRepository
import com.example.schoolerp.viewmodel.getStudentViewModel

class getStudentViewModelFactory(private val repository: getStudentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(getStudentViewModel::class.java)) {
            return getStudentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}