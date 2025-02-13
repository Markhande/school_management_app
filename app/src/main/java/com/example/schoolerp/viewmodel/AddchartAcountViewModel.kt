package com.example.schoolerp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.AddaccountchartResponse
import com.example.schoolerp.models.responses.PutTimeTableResponse
import com.example.schoolerp.repository.AddaccountchartRepository

class AddchartAcountViewModel(private val repository: AddaccountchartRepository):ViewModel() {
    val AddaccountchartResponse: MutableLiveData<AddaccountchartResponse> = MutableLiveData()

    fun AddchartAcount(paySalaryData: Map<String, String>) {
        repository.AddchartAcount(paySalaryData).observeForever { response ->
            AddaccountchartResponse.postValue(response)
        }
    }
}

