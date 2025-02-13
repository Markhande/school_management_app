package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.AddTimeTableResponse
import com.example.schoolerp.repository.AddTimeTableRepository

class AddTimeTableViewModel(private val repository: AddTimeTableRepository) : ViewModel() {

    fun addTimeTable(data: Map<String, String>): LiveData<Result<AddTimeTableResponse>> {
        return repository.addTimeTable(data)
    }
}
