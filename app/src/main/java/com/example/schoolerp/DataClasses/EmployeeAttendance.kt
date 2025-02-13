package com.example.schoolerp.DataClasses

data class EmployeeAttendance(
    val id: String,
    val date: String,
    val employee_id: String,
    val employee_name: String,
    var status: String,
    val school_id: String,
    val imgRes: Int? = null, // Optional image resource
    val employee_role:String,

)
