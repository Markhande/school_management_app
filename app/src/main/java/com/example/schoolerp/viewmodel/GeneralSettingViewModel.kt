package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolerp.models.responses.GetAccountSettingResponse
import com.example.schoolerp.repository.GeneralSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeneralSettingViewModel(private val repository: GeneralSettingRepository ) : ViewModel() {

    private val _accountSettings = MutableLiveData<GetAccountSettingResponse>()
    val accountSettings: LiveData<GetAccountSettingResponse> get() = _accountSettings

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAccountSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAccountSetting()
                if (response.isSuccessful) {
                    _accountSettings.postValue(response.body())
                } else {
                    _error.postValue("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
        }
    }
}