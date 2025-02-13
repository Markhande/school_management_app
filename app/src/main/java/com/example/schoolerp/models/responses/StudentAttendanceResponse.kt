package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.AttendanceData

data class StudentAttendanceResponse(
    val status: Boolean,
    val message: String,
    val data: AttendanceData?
)
