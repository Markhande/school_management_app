package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.GetgetCollectionStatusResponse
import com.example.schoolerp.models.responses.GetgetRevenueResponse
import com.example.schoolerp.repository.GetCollectionStatusRepository

class GetCollectionStatusViewModel(private val repository: GetCollectionStatusRepository):ViewModel(){
    private val _collection = MutableLiveData<GetgetCollectionStatusResponse>()
    val collection: LiveData<GetgetCollectionStatusResponse> get() = _collection

    // Fetching revenue data based on the schoolId
    fun fetchCollection(schoolId: String) {
        repository.getCollectionStatus(schoolId).observeForever { response ->
            // Updating LiveData when new data is available
            _collection.value = response
        }
    }
}

