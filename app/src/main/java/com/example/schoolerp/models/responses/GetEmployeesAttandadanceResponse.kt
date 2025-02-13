package com.example.schoolerp.models.responses

import com.example.schoolerp.DataClasses.EmployeeAttendance

class GetEmployeesAttandadanceResponse (
    val status: Boolean,
    val message: String,
    val data: List<EmployeeAttendance>
)