package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.SendMessageResponse
import com.example.schoolerp.repository.AddMessageRepository

class AddMessageViewModel(private val repository: AddMessageRepository) : ViewModel() {

    private val _sendMessageResponse = MutableLiveData<SendMessageResponse?>()
    val sendMessageResponse: LiveData<SendMessageResponse?> get() = _sendMessageResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun sendMessage(data: Map<String, String>) {
        repository.sendMessage(
            data,
            onSuccess = { response ->
                _sendMessageResponse.value = response
            },
            onFailure = { error ->
                _errorMessage.value = error
            }
        )
    }
}
