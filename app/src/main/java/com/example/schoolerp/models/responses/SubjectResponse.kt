package com.example.schoolerp.models.responses

data class SubjectResponse(
    val status: Boolean,
    val message: String,
    val data: List<ClassData>
)

data class ClassData(
    val classes: String,
    val subjects: List<Subject>
)

data class Subject(
    val id: String,
    val subject_name: String,
    val marks: String,
    val school_id: String
)
