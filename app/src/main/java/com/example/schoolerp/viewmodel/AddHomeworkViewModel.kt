package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.HomeWorkResponse
import com.example.schoolerp.repository.AddHomeworkRepository

class AddHomeworkViewModel(private val repository: AddHomeworkRepository) : ViewModel() {

    private val _homeworkResponse = MutableLiveData<HomeWorkResponse>()
    val homeworkResponse: LiveData<HomeWorkResponse> get() = _homeworkResponse

    fun addHomework(homeWorkData: Map<String, String>) {
        repository.addHomework(homeWorkData).observeForever { response ->
            _homeworkResponse.postValue(response)
        }
    }

    fun updatedHomework(homeWorkData: Map<String, String>) {
        repository.updatedHomework(homeWorkData).observeForever { response ->
            _homeworkResponse.postValue(response)
        }
    }
}
