package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.AddaddPaySalaryResponse
import com.example.schoolerp.repository.PaySalaryRepository

class PaySalaryViewModel(private val repository: PaySalaryRepository) : ViewModel() {
    val paySalaryResponse: MutableLiveData<AddaddPaySalaryResponse> = MutableLiveData()

    fun paySalary(paySalaryData: Map<String, String>) {
        repository.paySalary(paySalaryData).observeForever { response ->
            paySalaryResponse.postValue(response)
        }
    }
}
