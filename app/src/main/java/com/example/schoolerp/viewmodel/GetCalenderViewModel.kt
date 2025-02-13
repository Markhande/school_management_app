package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.GetCalanderResponse
import com.example.schoolerp.models.responses.GetgetCollectionStatusResponse
import com.example.schoolerp.repository.GetCalenderRepository

class GetCalenderViewModel (private val repository: GetCalenderRepository):ViewModel(){
    private val _Calender= MutableLiveData<GetCalanderResponse>()
    val Callender: LiveData<GetCalanderResponse> get() = _Calender

    // Fetching revenue data based on the schoolId
    fun fetchCalender(schoolId: String) {
        repository.getCalender(schoolId).observeForever { response ->
            // Updating LiveData when new data is available
            _Calender.value = response
        }
    }
}


