package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.requests.HomeworkHistory
import com.example.schoolerp.models.responses.SearchHomeWorkHistoryResponse
import com.example.schoolerp.repository.SearchHistoryHomeworkRepository

class SearchHistoryHomeworkViewModel(private val homeworkRepository: SearchHistoryHomeworkRepository) : ViewModel() {

    val homeworkHistory: MutableLiveData<List<HomeworkHistory>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun searchHomeworkHistory(searchParams: Map<String, String>) {
        loading.value = true
        homeworkRepository.searchHomeWorkHistory(searchParams).observeForever { result ->
            loading.value = false
            result.fold(
                onSuccess = { response ->
                    if (response.status) {
                        homeworkHistory.value = response.data
                    } else {
                        error.value = "Failed to fetch data"
                    }
                },
                onFailure = { throwable ->
                    error.value = throwable.message ?: "An error occurred"
                }
            )
        }
    }
}
