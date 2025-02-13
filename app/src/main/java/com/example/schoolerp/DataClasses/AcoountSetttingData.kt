package com.example.schoolerp.DataClasses

import com.google.gson.annotations.SerializedName


data class AcoountSetttingData(
    @SerializedName("id") val id: String,
    @SerializedName("school_name") val schoolName: String,
    @SerializedName("password") val password: String,
    @SerializedName("number") val number: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String
)
