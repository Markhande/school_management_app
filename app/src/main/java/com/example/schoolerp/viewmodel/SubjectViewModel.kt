package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ApiResponse
import com.example.schoolerp.repository.SubjectRepository


class SubjectViewModel(private val repository: SubjectRepository) : ViewModel() {
    val addSubjectResponse: MutableLiveData<ApiResponse> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    val apiResponse: LiveData<ApiResponse> get() = addSubjectResponse

    // Submit a single subject
    fun addSubject(fields: Map<String, String?>) {
        isLoading.value = true

        val response = repository.addSubject(fields)

        response.observeForever { result ->
            addSubjectResponse.value = result
            isLoading.value = false // Stop loading
        }
    }

    // Submit multiple subjects
    fun addMultipleSubjects(subjectList: List<Map<String, String?>>) {
        isLoading.value = true

        subjectList.forEach { fields ->
            val response = repository.addSubject(fields)

            response.observeForever { result ->
                // Collectively handle results (you can refine this logic for batch success/failure reporting)
                addSubjectResponse.value = result
                if (result.message.none()) {
                    isLoading.value = false
                    return@observeForever // Stop further processing if one fails
                }
            }
        }
        isLoading.value = false // End the loading once all submissions are processed
    }
}

