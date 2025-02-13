package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.GetTotalProfitResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.repository.GetTotalProfitRepository

class GetTotalProfitViewModel(private val repository: GetTotalProfitRepository ):ViewModel() {
    private val _profit=MutableLiveData<GetTotalProfitResponse>()
    val profit:LiveData<GetTotalProfitResponse> get() = _profit

    fun fetchProfit(schoolId: String) {
        repository.getTotalprofit(schoolId).observeForever { response ->
            _profit.value = response
        }

    }
}



