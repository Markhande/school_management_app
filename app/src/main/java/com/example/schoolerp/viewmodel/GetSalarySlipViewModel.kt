package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.Fragments.SalarySlip
import com.example.schoolerp.models.responses.GetSalarySlipResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.repository.GetSlipSalaryRepository

class GetSalarySlipViewModel(private val repository: GetSlipSalaryRepository):ViewModel (){
    private val _salaryslip = MutableLiveData<GetSalarySlipResponse>()
    val SalarySlip: LiveData<GetSalarySlipResponse> get() = _salaryslip

    fun fetchSlip(schoolId: String) {
        repository.getSalarySlip(schoolId).observeForever { response ->
            _salaryslip.value = response
        }
    }
}

