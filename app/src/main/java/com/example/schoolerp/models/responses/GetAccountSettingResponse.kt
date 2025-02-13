package com.example.schoolerp.models.responses

data class  GetAccountSettingResponse(
    val status: Boolean,
    val message: String,
    val data: List<GetAccountSettinga>
)
data class GetAccountSettinga(
    var id: String,
    val institute_logo: String,
    val school_name: String,
    val password: String,
    val tag_line: String,
    val number: String,
    val website: String,
    val addess: String,
    val country: String,
    val role: String,
    val school_id: String,
    val updated_at: String
)
