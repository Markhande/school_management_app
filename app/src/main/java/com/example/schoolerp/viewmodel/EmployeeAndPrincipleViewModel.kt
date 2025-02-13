package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.EmployeeAndPrinciple
import com.example.schoolerp.repository.EmployeeAndPrincipleRepository

class EmployeeAndPrincipleViewModel(private val repository: EmployeeAndPrincipleRepository) : ViewModel() {

    private val _employees = MutableLiveData<List<EmployeeAndPrinciple>>()
    val employees: LiveData<List<EmployeeAndPrinciple>> get() = _employees

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchTeachersAndPrincipals(schoolId: String) {
        repository.getTeacherPrinciple(
            schoolId = schoolId,
            onSuccess = { response ->
                _employees.postValue(response.data)
            },
            onError = { errorMessage ->
                _error.postValue(errorMessage)
            }
        )
    }
}
