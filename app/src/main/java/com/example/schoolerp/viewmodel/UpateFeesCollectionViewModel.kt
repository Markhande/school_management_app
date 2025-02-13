package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.StudentEditResponse
import com.example.schoolerp.models.responses.UpatedFeesCollectionResponse
import com.example.schoolerp.repository.UpatedFeesCollectionRepository

class UpateFeesCollectionViewModel(private val repository: UpatedFeesCollectionRepository)  : ViewModel() {
    private val _editCollectionResult = MutableLiveData<Result<UpatedFeesCollectionResponse>>()
    val editStudentResult: LiveData<Result<UpatedFeesCollectionResponse>> = _editCollectionResult

    fun editColleaction(data: Map<String, String>) {
        repository.updateFeesCollection(data).observeForever { result ->
            _editCollectionResult.postValue(result)
        }
    }
}

