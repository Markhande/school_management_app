package com.example.schoolerp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.getAcountChartRepository
import com.example.schoolerp.viewmodel.GetAcountChartViewModel
import com.example.schoolerp.viewmodel.GetCollectionStatusViewModel
import com.example.schoolerp.viewmodel.GetRevenueViewModel

class GetAcountChartViewModelFactory(
    private val repository: getAcountChartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GetAcountChartViewModel::class.java)) {
            GetAcountChartViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
