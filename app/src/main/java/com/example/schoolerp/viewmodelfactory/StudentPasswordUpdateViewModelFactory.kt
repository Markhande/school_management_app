package com.example.schoolerp.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.schoolerp.viewmodel.StudentPasswordUpdateViewModel

class StudentPasswordUpdateViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Ensure that the modelClass is of the correct type.
        if (modelClass.isAssignableFrom(StudentPasswordUpdateViewModel::class.java)) {
            return StudentPasswordUpdateViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
