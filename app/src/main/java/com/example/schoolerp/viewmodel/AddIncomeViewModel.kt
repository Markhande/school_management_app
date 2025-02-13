package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.AddIncomeData
import com.example.schoolerp.repository.AddIncomeRepository

class AddIncomeViewModel(private val repository: AddIncomeRepository) : ViewModel() {

    fun submitIncomeData(incomeData: Map<String, String>): LiveData<AddIncomeData> {
        return repository.submitIncomeData(incomeData)
    }
}
