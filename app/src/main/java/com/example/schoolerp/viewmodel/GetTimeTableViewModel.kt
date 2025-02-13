package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetTimeTableResponse
import com.example.schoolerp.repository.GetTimeTableRepository

class TimeTableViewModel(private val repository: GetTimeTableRepository) : ViewModel() {
    private val _deleteResult = MutableLiveData<Result<DeleteResponse>>()
    val deleteResult: LiveData<Result<DeleteResponse>> get() = _deleteResult

    private val _timeTableData = MutableLiveData<Result<GetTimeTableResponse>>()
    val timeTableData: LiveData<Result<GetTimeTableResponse>> get() = _timeTableData

    fun fetchTimeTable(schoolId: String) {
    //    _timeTableData.value = Result.loading() // Optional: Handle loading state
        val liveData = repository.fetchTimeTable(schoolId)
        liveData.observeForever {
            _timeTableData.value = it
        }
    }
    fun deleteTimeTable(schoolId: String, employeeId: String) {
        repository.deleteTimeTable(schoolId, employeeId).observeForever { result ->
            _deleteResult.value = result
        }
    }


}
