package com.example.schoolerp.models.responses

//data class InstituteProfileResponse(
//    val status: Boolean,
//    val message: String,
//    val id: String,
//    val school_id: String,
//    val role: String,
//    val number: String,
//    val created_at: String,
//    val school_name: String,
//    val institute_address: String,
//    val institute_logo: String,
//    val tag_line: String,
//    val phone: String,
//    val website: String,
//    val country: String,
//    val updated_at: String
//)

data class InstituteProfileResponse(
    val status: Boolean,
    val message: String,
    val data: InstituteDetails
)

data class InstituteDetails(
    val school_name: String,
    val institute_address: String,
    val institute_logo: String,
    val tag_line: String,
    val number: String,
    val website: String,
    val country: String,
    val updated_at: String
)


