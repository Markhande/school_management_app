package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolerp.DataClasses.InstituteProfileDataClass
import com.example.schoolerp.repository.InstituteProfileRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class InstituteProfileViewModel(private val repository: InstituteProfileRepository) : ViewModel() {

    private val _instituteResponse = MutableLiveData<InstituteProfileDataClass?>()
    val instituteResponse: LiveData<InstituteProfileDataClass?>
        get() = _instituteResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    fun submitInstitute(
        nameOfInstitute: String,
        phoneNumber: String,
        schoolId: String,
        address: String,
        targetLine: String,
        website: String?,
        country: String,
        imageLog: String
    ) {
        viewModelScope.launch {
            try {
                val response: Response<InstituteProfileDataClass> = repository.submitInstitute(
                    nameOfInstitute, phoneNumber, schoolId, address, targetLine, website, country, imageLog
                )
                if (response.isSuccessful) {
                    _instituteResponse.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }
}
