package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.AddaccountchartRepository
import com.example.schoolerp.viewmodel.AddExpenseViewModel
import com.example.schoolerp.viewmodel.AddchartAcountViewModel

class AdchartAcountViewModelFactory (private val repository: AddaccountchartRepository):ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddchartAcountViewModel::class.java)) {
            return AddchartAcountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
