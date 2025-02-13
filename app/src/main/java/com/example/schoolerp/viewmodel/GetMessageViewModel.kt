package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.DeleteResponse
import com.example.schoolerp.models.responses.GetMessageResponse
import com.example.schoolerp.repository.GetMessageRepository

class GetMessageViewModel(private val repository: GetMessageRepository) : ViewModel() {

    private val _messages = MutableLiveData<GetMessageResponse?>()
    val messages: LiveData<GetMessageResponse?> get() = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _deleteResult = MutableLiveData<Result<DeleteResponse>>()
    val deleteResult: LiveData<Result<DeleteResponse>> get() = _deleteResult


    fun fetchMessages(schoolId: String, employee: String) {
        _isLoading.value = true
        repository.getMessages(schoolId, employee).observeForever { response ->
            _isLoading.value = false
            if (response != null) {
                _messages.value = response
            } else {
                _error.value = "Failed to fetch messages."
            }
        }
    }
    fun deleteHomeWork(schoolId: String, Id: String) {
        repository.deleteMessage(schoolId, Id).observeForever { result ->
            _deleteResult.value = result
        }
    }

}


