package com.example.schoolerp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.Homework
import com.example.schoolerp.models.responses.DeleteHomeworkResponse
import com.example.schoolerp.models.responses.GetHomeworkResponse
import com.example.schoolerp.repository.GetHomeworkRepository

class GetHomeworkViewModel(private val repository: GetHomeworkRepository) : ViewModel() {

    private val _homeworkList = MutableLiveData<List<Homework>?>()
    val homeworkList: MutableLiveData<List<Homework>?> get() = _homeworkList
    private val _deleteHomeworkResult = MutableLiveData<Result<DeleteHomeworkResponse>>()
    val deleteHomeworkResult: LiveData<Result<DeleteHomeworkResponse>> get() = _deleteHomeworkResult

    fun getHomeWork(schoolId: String, teacher: String) {
        repository.fetchHomeWork(schoolId, teacher).observeForever { result ->
            result.onSuccess { response ->
                // Assuming `response` has a `homeworkList` property
                _homeworkList.postValue(response.data)
            }.onFailure { error ->
                // Handle the error (e.g., show a message to the user)
                Log.e("GetHomeworkViewModel", "Error fetching homework", error)
            }
        }
    }
    fun deleteHomework(schoolId: String, homeworkId: String) {
        repository.deleteHomework(schoolId, homeworkId).observeForever {
            _deleteHomeworkResult.value = it
        }
    }
    fun removeHomeworkFromList(homework: Homework) {
        val updatedList = _homeworkList.value?.toMutableList()
        updatedList?.remove(homework)  // Remove the homework from the list
        _homeworkList.postValue(updatedList)  // Update the list in LiveData
    }


}
