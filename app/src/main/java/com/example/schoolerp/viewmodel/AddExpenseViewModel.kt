package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.AddExpenseData
import com.example.schoolerp.DataClasses.AddIncomeData
import com.example.schoolerp.models.responses.ApiResponse
import com.example.schoolerp.repository.AddExpenseRepository
import com.example.schoolerp.repository.EmployeeRepository

class AddExpenseViewModel(private val repository: AddExpenseRepository) : ViewModel() {


    fun submitExpenseData(expenseData: Map<String, String>): LiveData<AddExpenseData> {
        return repository.submitExpenseData(expenseData)
    }
}