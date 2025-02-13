package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.models.responses.ClassWiseSubjectResponse
import com.example.schoolerp.repository.ClassWiseSubjectRepository

class ClassWiseSubjectViewModel(private val repository: ClassWiseSubjectRepository) : ViewModel() {

    fun getSubjectsByClass(classWiseSubjectData: Map<String, String>): LiveData<ClassWiseSubjectResponse> {
        return repository.fetchSubjectsByClass(classWiseSubjectData)
    }
}
