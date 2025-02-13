package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.ClassWithSubjects

data class SubjectDataResponse(
    val status: Boolean,
    val message: String,
    val data: List<ClassWithSubjects>
)
