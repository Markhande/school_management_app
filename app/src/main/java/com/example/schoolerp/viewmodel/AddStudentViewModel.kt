package com.example.schoolerp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ApiResponse
import com.example.schoolerp.repository.AddStudentRepository

class AddStudentViewModel(private val repository: AddStudentRepository) : ViewModel() {
    private val _response = MutableLiveData<ApiResponse?>()
    val response: LiveData<ApiResponse?> get() = _response

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun addStudent(studentMap: Map<String, String>) {
        repository.addStudent(studentMap) { apiResponse, errorMessage ->
            apiResponse?.let {
                _response.value = it
            } ?: run {
                _error.value = errorMessage
            }
        }
    }
}
