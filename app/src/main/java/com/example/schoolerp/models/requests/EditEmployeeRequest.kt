package com.example.schoolerp.models.requests

data class EditEmployeeRequest(
    val name: String,
    val designation: String,
    val department: String,
    val contact: String,
    val email: String
)
