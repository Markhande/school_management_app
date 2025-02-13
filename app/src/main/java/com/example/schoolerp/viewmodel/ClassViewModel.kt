package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.ClassResponseData
import com.example.schoolerp.repository.ClassRepository

// method not allowed
class ClassViewModel(private val repository: ClassRepository) : ViewModel() {

    fun addClass(classData: Map<String, String>): LiveData<ClassResponseData> {
        return repository.addClass(classData)
    }
}