package com.example.schoolerp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.Api.ApiService
import com.example.schoolerp.models.responses.PutHomeWorkResponse
import com.example.schoolerp.repository.PutMessageRepository

class PutMessageViewModel(private val repository: PutMessageRepository) : ViewModel() {
    val putHomeWorkResponse: MutableLiveData<PutHomeWorkResponse> = MutableLiveData()

    fun putMessage(paySalaryData: Map<String, String>) {
        repository.PutHomeWork(paySalaryData).observeForever { response ->
            putHomeWorkResponse.postValue(response)
        }
    }
}
