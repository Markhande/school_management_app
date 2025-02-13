package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.GetOverViewResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.repository.getOverviewRepository

class getOverViewModel(private val repository: getOverviewRepository): ViewModel() {
    private val _OverView = MutableLiveData<GetOverViewResponse?>()
    val overviewchart: MutableLiveData<GetOverViewResponse?> get() = _OverView

    fun fetchOverView(schoolId: String) {
        repository.getOverview(schoolId).observeForever { response ->
            _OverView.value = response
        }
    }
}
