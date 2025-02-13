package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.AddCalander
import com.example.schoolerp.models.responses.AddMarkGradingResponse
import com.example.schoolerp.repository.AddCalenderRepository
import java.util.ServiceLoader.Provider

class AddCalenderViewModel(private val repository: AddCalenderRepository):ViewModel() {
    private val _CalenderResponse = MutableLiveData<AddCalander>()
    val CalenderResponse: LiveData<AddCalander> get() = _CalenderResponse

    fun addCalander(clanderdata: Map<String, String>) {
        repository.addcalander(clanderdata).observeForever { response ->
            _CalenderResponse.postValue(response)
        }
    }


}