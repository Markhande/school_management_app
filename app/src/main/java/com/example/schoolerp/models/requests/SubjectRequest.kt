package com.example.schoolerp.models.requests

import com.google.gson.annotations.SerializedName


data class SubjectRequest(
    @SerializedName("class") val className: String,
    @SerializedName("subject_name") val subjectName: String,
    @SerializedName("marks") val marks: String,
    @SerializedName("school_id") val schoolId: Int
)
