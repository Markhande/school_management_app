package com.example.schoolerp.DataClasses

import java.io.Serializable

data class ClassWithSubjects(
    val classes: String,
    val class_name: String,
    val subjects: List<Subject>
): Serializable

data class Subject(
    val id: String,
    val subject_name: String,
    val marks: String,
    val school_id: String
): Serializable
