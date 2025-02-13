package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.AddFeeParticularResponse
import com.example.schoolerp.repository.AddFeeParticularRepository

class AddFeeParticularViewModel(private val repository: AddFeeParticularRepository) : ViewModel() {

    private val _addFeeParticularResponse = MutableLiveData<AddFeeParticularResponse>()
    val addFeeParticularResponse: LiveData<AddFeeParticularResponse> get() = _addFeeParticularResponse

    fun addFeeParticular(addFeeParticularData: Map<String, String>) {
        repository.addFeeParticular(addFeeParticularData).observeForever { response ->
            _addFeeParticularResponse.postValue(response)
        }
    }
}