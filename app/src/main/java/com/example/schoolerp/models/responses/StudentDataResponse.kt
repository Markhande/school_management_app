package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.Student

data class StudentDataResponse(
    val status: Boolean,
    val message: String,
    val data: List<Student>
)
