package com.example.schoolerp.DataClasses

data class AttendanceStudentDateWise(
    val class_id: String,
    val class_name: String,
    val students: List<Student>
)

