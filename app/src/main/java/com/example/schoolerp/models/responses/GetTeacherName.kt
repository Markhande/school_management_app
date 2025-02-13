package com.example.schoolerp.models.responses

import com.google.gson.annotations.SerializedName

data class GetTeacherNames(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("employee_name")
    val employeeName: String? = null,

    @SerializedName("employee_role")
    val techer: String? = null
)
