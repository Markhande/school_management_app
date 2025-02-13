package com.example.schoolerp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.AddaddPaySalaryResponse
import com.example.schoolerp.models.responses.PutTimeTableResponse
import com.example.schoolerp.repository.PutTimeTableRepository

class PutTimeTableViewModel(private val repository: PutTimeTableRepository):ViewModel (){
    val putTimeTableResponse: MutableLiveData<PutTimeTableResponse> = MutableLiveData()

    fun putTimeTable(paySalaryData: Map<String, String>) {
        repository.PutTimeTable(paySalaryData).observeForever { response ->
            putTimeTableResponse.postValue(response)
        }
    }
}
