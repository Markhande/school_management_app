package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.FeeParticularResponse
import com.example.schoolerp.repository.GetFeeParticularRepository

class GetFeeParticularViewModel (private val repository: GetFeeParticularRepository) : ViewModel() {
    fun fetchFeeParticulars(schoolId: String, className: String): LiveData<FeeParticularResponse?> {
        return repository.getFeeParticulars(schoolId, className)
    }
}