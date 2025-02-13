package com.example.schoolerp.models.responses

data class getStudent_Idcard_Response(
    val status: Boolean,
    val message: String,
    val data: List<StudentIdCardDetails>
)

data class StudentIdCardDetails(
    val st_name: String,
    val id: String,
    val picture: String,
    val dt_of_birth: String?,
    val st_class: String,
    val school_name: String,
    val address: String
)
