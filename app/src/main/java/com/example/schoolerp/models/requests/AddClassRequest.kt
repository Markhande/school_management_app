package com.example.schoolerp.models.requests

import com.google.gson.annotations.SerializedName

data class AddClassRequest(
  /*  @SerializedName("id")
    val id: Int,*/
    @SerializedName("class_name")
    val className: String,
/*    @SerializedName("tution_fees")
    val tutionFees: String,*/
    @SerializedName("class_teacher")
    val classTeacher: String,
    @SerializedName("school_id")
    val schoolId: String
)
