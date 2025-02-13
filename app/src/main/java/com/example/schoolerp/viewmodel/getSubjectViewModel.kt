package com.example.schoolerp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.schoolerp.DataClasses.ClassWithSubjects
import com.example.schoolerp.SchoolId.SchoolId
import com.example.schoolerp.repository.getSubjectRepository

class getSubjectViewModel(
    private val repository: getSubjectRepository,
    private val schoolId: String
) : ViewModel() {

    // LiveData to hold the list of classWithSubjects
    val classWithSubjects: LiveData<List<ClassWithSubjects>> = repository.getSubjects(schoolId)
}

