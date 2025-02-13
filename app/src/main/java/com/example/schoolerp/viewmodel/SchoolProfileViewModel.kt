package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.repository.InstituteRepository

class SchoolProfileViewModel(private val repository: InstituteRepository) : ViewModel() {

    private val _schoolProfile = MutableLiveData<InstituteProfileResponse>()
    val schoolProfile: LiveData<InstituteProfileResponse> = _schoolProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchSchoolProfile(schoolId: String) {
        repository.getInstituteProfile(schoolId).observeForever { result ->
            result.onSuccess {
                _schoolProfile.value = it
            }
            result.onFailure {
                _error.value = it.message
            }
        }
    }
}
