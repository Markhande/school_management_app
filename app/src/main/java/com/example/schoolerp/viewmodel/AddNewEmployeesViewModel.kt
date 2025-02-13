package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ApiResponse
import com.example.schoolerp.repository.EmployeeRepository

class AddNewEmployeesViewModel(private val repository: EmployeeRepository) : ViewModel() {

    val apiResponse: LiveData<ApiResponse> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun addNewEmployee(employeeData: Map<String, String?>) {
        isLoading.value = true
        val response = repository.addEmployee(employeeData)

        response.observeForever { result ->
            (apiResponse as MutableLiveData).value = result
            isLoading.value = false
        }
    }
}
