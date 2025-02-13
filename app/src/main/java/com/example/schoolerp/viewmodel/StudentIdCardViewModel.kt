package com.example.schoolerp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.schoolerp.models.responses.StudentIdCardDetails
import com.example.schoolerp.repository.StudentRepository

class StudentIdCardViewModel(application: Application) : AndroidViewModel(application) {

    private val studentRepository = StudentRepository()
    private val _studentList = MutableLiveData<List<StudentIdCardDetails>>()
    val studentList: LiveData<List<StudentIdCardDetails>> = _studentList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchStudentIdCardData(schoolId: String) {
        studentRepository.getStudentIdCardDetails(schoolId) { result ->
            result.onSuccess {
                _studentList.value = it.data // Assuming 'data' contains the list of students
            }
            result.onFailure {
                _errorMessage.value = it.localizedMessage
            }
        }
    }
}