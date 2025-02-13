package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolerp.models.requests.HomeworkHistory
import com.example.schoolerp.models.responses.BaseOnInputMessageResponse
import com.example.schoolerp.repository.BaseOnInputeMessageRepository
import kotlinx.coroutines.launch

class BaseOnInputMessageViewModel(private val repository: BaseOnInputeMessageRepository) : ViewModel() {

    private val _baseOnInputMessageResponse = MutableLiveData<Result<BaseOnInputMessageResponse>>()
    val baseOnInputMessageResponse: LiveData<Result<BaseOnInputMessageResponse>>
        get() = _baseOnInputMessageResponse

    fun getBaseOnInputMessage(inputMap: Map<String, String>) {
        viewModelScope.launch {
            val liveData = repository.fetchBaseOnInputMessage(inputMap)
            liveData.observeForever { result ->
                _baseOnInputMessageResponse.value = result
            }
        }
    }
}
