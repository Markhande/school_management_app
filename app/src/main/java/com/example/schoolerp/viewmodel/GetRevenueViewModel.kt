package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.repository.GetRevenueRepository

class GetRevenueViewModel(private val repository: GetRevenueRepository) : ViewModel() {

    private val _revenue = MutableLiveData<GetgetRevenueResponse>()
    val revenue: LiveData<GetgetRevenueResponse> get() = _revenue

    fun fetchRevenue(schoolId: String) {
        repository.getRevenue(schoolId).observeForever { response ->
            _revenue.value = response
        }
    }
}
