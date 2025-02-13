package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.AddMarkGradingResponse
import com.example.schoolerp.models.responses.HomeWorkResponse
import com.example.schoolerp.repository.AddMarkGradingRepository

class AddMarkGradingViewModel(private val repository: AddMarkGradingRepository):ViewModel() {
    private val _GradingResponse = MutableLiveData<AddMarkGradingResponse>()
    val GradingResponse: LiveData<AddMarkGradingResponse> get() = _GradingResponse

    fun addMarkGrading(MarkGradingData: Map<String, String>) {
        repository.addMarkGrading(MarkGradingData).observeForever { response ->
            _GradingResponse.postValue(response)
        }
    }

}