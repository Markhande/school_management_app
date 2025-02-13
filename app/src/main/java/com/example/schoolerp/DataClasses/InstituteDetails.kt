package com.example.schoolerp.DataClasses

data class InstituteDetails(
    val name_of_institute: String,
    val target_line: String,
    val number: String,
    val address: String,
    val website: String?,
    val country: String?,
    val institute_logo: String,
    val id: Int
)