package com.example.schoolerp.DataClasses

data class AbsentStudent(
    val id: String,
    val studentId: String,
    val date: String,
    val studentClass: String,
    val st_name: String,
    val fatherName: String,
    val status: String,
    val schoolId: String,
    val imgRes: Int? = null
)
