package com.example.schoolerp.DataClasses

data class AttendanceData(
    val st_name: String,
    val status: String,
    val date: String,
    val school_id: String,
    val `class`: String,
    val student_id: String,
    val id: Int
)
