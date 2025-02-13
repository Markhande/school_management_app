package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetAcountChartResponse
import com.example.schoolerp.models.responses.GetTotalProfitResponse
import com.example.schoolerp.repository.getAcountChartRepository

class GetAcountChartViewModel(private val repository: getAcountChartRepository) : ViewModel() {
    private val _AcountChart = MutableLiveData<GetAcountChartResponse>()
    val AcountChart: LiveData<GetAcountChartResponse> get() = _AcountChart
    private val _deleteResult = MutableLiveData<Result<DeleteResponse>>()
    val deleteResult: LiveData<Result<DeleteResponse>> get() = _deleteResult

    fun fetchAcountChart(schoolId: String) {
        repository.GetAcountChart(schoolId).observeForever { response ->
            _AcountChart.value = response
        }
    }
    fun deleteAcountDelete(schoolId: String, Id: String) {
        repository.deleteAcountChart(schoolId, Id).observeForever { result ->
            _deleteResult.value = result
        }
    }

}
