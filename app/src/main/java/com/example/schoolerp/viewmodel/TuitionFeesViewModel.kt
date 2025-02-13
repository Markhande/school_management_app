package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ClassAndStudentGetTuitionResponse
import com.example.schoolerp.repository.TuitionFeesRepository

class TuitionFeesViewModel(private val repository: TuitionFeesRepository) : ViewModel() {

    private val _tuitionData = MutableLiveData<ClassAndStudentGetTuitionResponse>()
    val tuitionData: LiveData<ClassAndStudentGetTuitionResponse> get() = _tuitionData

    fun fetchTuitionData(params: Map<String, String>) {
        repository.getTuitionData(params).observeForever { response ->
            _tuitionData.postValue(response)
        }
    }
}
