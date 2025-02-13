package com.example.schoolerp.models.responses

//data class GetAccountSetting(
//    val id: String,
//    val school_name: String,
//    val password: String,
//    val number: String,
//    val created_at: String,
//    val school_id: String,
//    val role: String,
//    val email: String
//)

data class GetAccountSetting(
    val status: Boolean,
    val message: String,
)

